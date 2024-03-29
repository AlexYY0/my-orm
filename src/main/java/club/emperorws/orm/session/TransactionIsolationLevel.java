package club.emperorws.orm.session;

import java.sql.Connection;

/**
 * SQL执行事务隔离级别
 *
 * @author: EmperorWS
 * @date: 2023/5/8 17:01
 * @description: TransactionIsolationLevel: SQL执行事务隔离级别
 */
public enum TransactionIsolationLevel {
    NONE(Connection.TRANSACTION_NONE),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
    /**
     * A non-standard isolation level for Microsoft SQL Server.
     * Defined in the SQL Server JDBC driver {@link com.microsoft.sqlserver.jdbc.ISQLServerConnection}
     *
     * @since 3.5.6
     */
    SQL_SERVER_SNAPSHOT(0x1000);

    private final int level;

    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
