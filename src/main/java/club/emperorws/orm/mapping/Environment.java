package club.emperorws.orm.mapping;

import club.emperorws.orm.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * 设置事务与数据库连接dataSource
 *
 * @author: EmperorWS
 * @date: 2023/5/10 16:09
 * @description: Environment: 设置事务与数据库连接dataSource
 */
public final class Environment {
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;

    public Environment(TransactionFactory transactionFactory, DataSource dataSource) {
        if (transactionFactory == null) {
            throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
        }
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Environment build() {
            return new Environment(this.transactionFactory, this.dataSource);
        }

    }

    public TransactionFactory getTransactionFactory() {
        return this.transactionFactory;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

}
