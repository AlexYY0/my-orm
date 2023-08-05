package club.emperorws.orm.statement;

import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.parameter.ParameterHandler;
import club.emperorws.orm.result.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * StatementHandler的包装器类（装饰着模式）
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:15
 * @description: RoutingStatementHandler: StatementHandler的包装器类
 */
public class RoutingStatementHandler implements StatementHandler {

    private final StatementHandler delegate;

    public RoutingStatementHandler(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        switch (ms.getStatementType()) {
            case STATEMENT:
                delegate = new SimpleStatementHandler(ms, parameter, rowBounds, resultHandler, boundSql);
                break;
            case PREPARED:
                delegate = new PreparedStatementHandler(ms, parameter, rowBounds, resultHandler, boundSql);
                break;
            case CALLABLE:
                //暂时不做
                //delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
                //break;
            default:
                throw new OrmException("Unknown statement type: " + ms.getStatementType());
        }

    }

    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        return delegate.prepare(connection, transactionTimeout);
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        delegate.parameterize(statement);
    }

    @Override
    public void batch(Statement statement) throws SQLException {
        delegate.batch(statement);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        return delegate.update(statement);
    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        return delegate.query(statement, resultHandler);
    }

    @Override
    public <E> Cursor<E> queryCursor(Statement statement) throws SQLException {
        return delegate.queryCursor(statement);
    }

    @Override
    public BoundSql getBoundSql() {
        return delegate.getBoundSql();
    }

    @Override
    public ParameterHandler getParameterHandler() {
        return delegate.getParameterHandler();
    }
}
