package club.emperorws.orm.transaction.jdbc;

import club.emperorws.orm.session.TransactionIsolationLevel;
import club.emperorws.orm.transaction.Transaction;
import club.emperorws.orm.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * JDBC事务的创建工厂
 *
 * @author: EmperorWS
 * @date: 2023/5/8 17:20
 * @description: JdbcTransactionFactory: JDBC事务的创建工厂
 */
public class JdbcTransactionFactory implements TransactionFactory {

    private boolean skipSetAutoCommitOnClose;

    /**
     * 为事务设置一些格外的属性配置：开关什么的（ignore，暂时没什么用）
     *
     * @param props 配置信息
     */
    @Override
    public void setProperties(Properties props) {
        if (props == null) {
            return;
        }
        String value = props.getProperty("skipSetAutoCommitOnClose");
        if (value != null) {
            skipSetAutoCommitOnClose = Boolean.parseBoolean(value);
        }
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(ds, level, autoCommit, skipSetAutoCommitOnClose);
    }
}
