package club.emperorws.orm.statement;

import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.parameter.ParameterHandler;
import club.emperorws.orm.result.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * SQL语句的执行处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:07
 * @description: StatementHandler: SQL语句的执行处理器
 */
public interface StatementHandler {

    /**
     * 预编译sql
     */
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    /**
     * 预编sql的参数设置
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * batch模式的执行
     */
    void batch(Statement statement) throws SQLException;

    /**
     * 执行insert、delete、update
     */
    int update(Statement statement) throws SQLException;

    /**
     * 值select
     */
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

    /**
     * ignore
     */
    <E> Cursor<E> queryCursor(Statement statement) throws SQLException;

    BoundSql getBoundSql();

    ParameterHandler getParameterHandler();
}
