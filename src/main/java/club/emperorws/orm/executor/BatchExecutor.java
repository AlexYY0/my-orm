package club.emperorws.orm.executor;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.BatchExecutorException;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.statement.StatementHandler;
import club.emperorws.orm.transaction.Transaction;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Batch批处理模式下的SQL语句执行器
 *
 * @author: EmperorWS
 * @date: 2023/5/12 15:29
 * @description: BatchExecutor: Batch批处理模式下的SQL语句执行器
 */
public class BatchExecutor extends BaseExecutor {

    public static final int BATCH_UPDATE_RETURN_VALUE = Integer.MIN_VALUE + 1002;

    /**
     * 需要执行的SQL语句Statement
     */
    private final List<Statement> statementList = new ArrayList<>();

    /**
     * Batch批处理SQL执行结果存储器
     */
    private final List<BatchResult> batchResultList = new ArrayList<>();

    /**
     * 当前执行的SQL语句，预编译后的SQL语句
     */
    private String currentSql;

    /**
     * 当前正在执行的SQL语句对象
     */
    private MappedStatement currentStatement;

    public BatchExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
        final Configuration configuration = ms.getConfiguration();
        final StatementHandler handler = configuration.newStatementHandler(ms, parameterObject, RowBounds.DEFAULT, null, null);
        final BoundSql boundSql = handler.getBoundSql();
        final String sql = boundSql.getSql();
        final Statement stmt;
        //判断是否是同一批SQL（SQL语句一样）
        //todo 这里有待优化，如何判定等于
        if (sql.equals(currentSql) && ms.getId().equals(currentStatement.getId())) {
            //是同一批，直接设置参数，然后Statement.addBatch()执行即可
            int last = statementList.size() - 1;
            stmt = statementList.get(last);
            applyTransactionTimeout(stmt);
            //设置SQL的参数（？占位符参数的设置）
            handler.parameterize(stmt);
            BatchResult batchResult = batchResultList.get(last);
            batchResult.addParameterObject(parameterObject);
        } else {
            //不是同一批
            Connection connection = getConnection(ms.getStatementLog());
            //预编译SQL
            stmt = handler.prepare(connection, transaction.getTimeout());
            //设置SQL的参数（？占位符参数的设置）
            handler.parameterize(stmt);
            currentSql = sql;
            currentStatement = ms;
            statementList.add(stmt);
            batchResultList.add(new BatchResult(ms, sql, parameterObject));
        }
        //Statement.addBatch()，后续直接Statement.executeBatch()执行
        handler.batch(stmt);
        return BATCH_UPDATE_RETURN_VALUE;
    }

    /**
     * Batch批处理模式下的查询逻辑
     * <p>代码等同于{@link SimpleExecutor#doQuery(MappedStatement, Object, RowBounds, ResultHandler, BoundSql)}</p>
     */
    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            //清空Statement（避免有遗留的情况）
            flushStatements();
            Configuration configuration = ms.getConfiguration();
            //获取SQL语句的执行处理器
            StatementHandler handler = configuration.newStatementHandler(ms, parameterObject, rowBounds, resultHandler, boundSql);
            //获取连接
            Connection connection = getConnection(ms.getStatementLog());
            //预编译SQL
            stmt = handler.prepare(connection, transaction.getTimeout());
            //设置SQL的参数（？占位符参数的设置）
            handler.parameterize(stmt);
            //查询
            return handler.query(stmt, resultHandler);
        } finally {
            //关闭Statement
            closeStatement(stmt);
        }
    }

    /**
     * Batch批处理模式下的查询逻辑
     * <p>代码等同于{@link SimpleExecutor#doQueryCursor(MappedStatement, Object, RowBounds, BoundSql)}</p>
     */
    @Override
    protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
        //清空Statement（避免有遗留的情况）
        flushStatements();
        Configuration configuration = ms.getConfiguration();
        //获取SQL语句的执行处理器
        StatementHandler handler = configuration.newStatementHandler(ms, parameter, rowBounds, null, boundSql);
        //获取连接
        Connection connection = getConnection(ms.getStatementLog());
        //预编译SQL
        Statement stmt = handler.prepare(connection, transaction.getTimeout());
        //设置SQL的参数（？占位符参数的设置）
        handler.parameterize(stmt);
        //查询
        Cursor<E> cursor = handler.queryCursor(stmt);
        //close()
        stmt.closeOnCompletion();
        return cursor;
    }

    /**
     * 真正的Batch批处理的SQL语句执行（Statement.executeBatch()）
     *
     * @param isRollback 是否是Rollback回滚在执行FlushStatements
     * @return Batch批处理模式的执行结果
     * @throws SQLException 异常
     */
    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
        try {
            List<BatchResult> results = new ArrayList<>();
            if (isRollback) {
                return Collections.emptyList();
            }
            for (int i = 0, n = statementList.size(); i < n; i++) {
                Statement stmt = statementList.get(i);
                applyTransactionTimeout(stmt);
                BatchResult batchResult = batchResultList.get(i);
                try {
                    batchResult.setUpdateCounts(stmt.executeBatch());
                    //主键回显不要了
                    // Close statement，顺便也不用Statement.clearBatch()
                    closeStatement(stmt);
                } catch (BatchUpdateException e) {
                    StringBuilder message = new StringBuilder();
                    message.append(batchResult.getMappedStatement().getId())
                            .append(" (batch index #")
                            .append(i + 1)
                            .append(")")
                            .append(" failed.");
                    if (i > 0) {
                        message.append(" ")
                                .append(i)
                                .append(" prior sub executor(s) completed successfully, but will be rolled back.");
                    }
                    throw new BatchExecutorException(message.toString(), e, results, batchResult);
                }
                results.add(batchResult);
            }
            return results;
        } finally {
            for (Statement stmt : statementList) {
                //关闭Statement
                closeStatement(stmt);
            }
            currentSql = null;
            statementList.clear();
            batchResultList.clear();
        }
    }
}
