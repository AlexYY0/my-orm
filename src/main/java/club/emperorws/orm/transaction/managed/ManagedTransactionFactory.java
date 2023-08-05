package club.emperorws.orm.transaction.managed;

import club.emperorws.orm.session.TransactionIsolationLevel;
import club.emperorws.orm.transaction.Transaction;
import club.emperorws.orm.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * 外部托管型TransactionFactory
 *
 * @author: EmperorWS
 * @date: 2023/5/10 16:15
 * @description: ManagedTransactionFactory: 管理型TransactionFactory
 */
public class ManagedTransactionFactory implements TransactionFactory {

    private boolean closeConnection = true;

    @Override
    public void setProperties(Properties props) {
        if (props != null) {
            String closeConnectionProperty = props.getProperty("closeConnection");
            if (closeConnectionProperty != null) {
                closeConnection = Boolean.parseBoolean(closeConnectionProperty);
            }
        }
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new ManagedTransaction(conn, closeConnection);
    }

    @Override
    public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
        //忽略事务隔离级别TransactionIsolationLevel与自动提交设置autoCommit
        //完全交给外部去控制
        return new ManagedTransaction(ds, level, closeConnection);
    }
}
