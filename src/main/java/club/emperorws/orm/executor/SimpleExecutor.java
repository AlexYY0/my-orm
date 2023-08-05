package club.emperorws.orm.executor;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.logging.Log;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.statement.StatementHandler;
import club.emperorws.orm.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * 简单SQL语句的执行器
 *
 * @author: EmperorWS
 * @date: 2023/5/12 14:43
 * @description: SimpleExecutor: 简单SQL语句的执行者
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 获取SQL语句的执行处理器
            StatementHandler handler = configuration.newStatementHandler(ms, parameter, RowBounds.DEFAULT, null, null);
            // 预编译SQL，并获取sql对应的Statement
            stmt = prepareStatement(handler, ms.getStatementLog());
            // 执行SQL（内部会关闭ResultSet，每次处理完执行结果，就会ResultSet.close()）
            return handler.update(stmt);
        } finally {
            // 关闭Statement
            closeStatement(stmt);
        }
    }

    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 获取SQL语句的执行处理器
            StatementHandler handler = configuration.newStatementHandler(ms, parameter, rowBounds, resultHandler, boundSql);
            // 预编译SQL，并获取sql对应的Statement
            stmt = prepareStatement(handler, ms.getStatementLog());
            // 执行SQL（内部会关闭ResultSet，每次处理完执行结果，就会ResultSet.close()）
            return handler.query(stmt, resultHandler);
        } finally {
            // 关闭Statement
            closeStatement(stmt);
        }
    }

    @Override
    protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
        Configuration configuration = ms.getConfiguration();
        // 获取SQL语句的执行处理器
        StatementHandler handler = configuration.newStatementHandler(ms, parameter, rowBounds, null, boundSql);
        // 预编译SQL，并获取sql对应的Statement
        Statement stmt = prepareStatement(handler, ms.getStatementLog());
        // 执行SQL
        Cursor<E> cursor = handler.queryCursor(stmt);
        // 关闭Cursor
        stmt.closeOnCompletion();
        return cursor;
    }

    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) {
        return Collections.emptyList();
    }

    /**
     * 预编译SQL，并设置占位符的参数
     *
     * @param handler      sql语句执行器
     * @param statementLog 该Mapper method的执行日志log
     * @return 预编译后的 Statement
     * @throws SQLException 异常
     */
    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
        Statement stmt;
        // 获取连接
        Connection connection = getConnection(statementLog);
        // 预编译SQL
        stmt = handler.prepare(connection, transaction.getTimeout());
        // 设置SQL的参数（？占位符参数的设置）
        handler.parameterize(stmt);
        return stmt;
    }
}
