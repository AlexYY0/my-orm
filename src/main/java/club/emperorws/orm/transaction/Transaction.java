package club.emperorws.orm.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务处理器接口
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:20
 * @description: Transaction: 事务处理器接口
 */
public interface Transaction {
    
    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;

    Integer getTimeout() throws SQLException;
}
