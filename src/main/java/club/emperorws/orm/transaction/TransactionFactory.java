package club.emperorws.orm.transaction;

import club.emperorws.orm.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * 事务的创建工厂
 *
 * @author: EmperorWS
 * @date: 2023/5/8 17:14
 * @description: TransactionFactory: 事务的创建工厂
 */
public interface TransactionFactory {

    /**
     * 为事务设置一些格外的属性配置：开关什么的（ignore，暂时没什么用）
     *
     * @param props 配置信息
     */
    default void setProperties(Properties props) {
        // NOP
    }

    /**
     * 创建一个{@link Transaction}事务
     *
     * @param conn 已经存在的数据库连接
     * @return Transaction
     */
    Transaction newTransaction(Connection conn);

    /**
     * 创建一个{@link Transaction}事务
     *
     * @param dataSource 通过DataSource创建一个数据库连接
     * @param level      事务隔离级别
     * @param autoCommit 事务是否自动提交
     * @return Transaction
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}
