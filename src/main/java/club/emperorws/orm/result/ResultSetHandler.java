package club.emperorws.orm.result;

import club.emperorws.orm.cursor.Cursor;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * SQL结果处理器（数据库查出来的数据-->对象结果）
 *
 * @author: EmperorWS
 * @date: 2023/4/28 15:17
 * @description: ResultSetHandler: SQL结果处理器
 */
public interface ResultSetHandler {

    /**
     * 结果集处理
     *
     * @param stmt Statement执行sql
     * @param <E>  结果类型
     * @return 执行结果
     * @throws SQLException 异常
     */
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

    /**
     * 流式查询游标结果集处理，ignore
     */
    <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

    /**
     * 存储过程结果集处理，ignore
     */
    void handleOutputParameters(CallableStatement cs) throws SQLException;
}
