package club.emperorws.orm.executor;

import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * SQL语句的执行者接口
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:18
 * @description: Executor: SQL语句的执行者接口
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    int update(MappedStatement ms, Object parameter) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

    List<BatchResult> flushStatements() throws SQLException;

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);
}
