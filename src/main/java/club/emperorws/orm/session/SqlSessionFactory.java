package club.emperorws.orm.session;

import club.emperorws.orm.Configuration;

import java.sql.Connection;

/**
 * SqlSession创建工厂接口
 *
 * @author: EmperorWS
 * @date: 2023/4/28 12:05
 * @description: SqlSessionFactory: SqlSession创建工厂接口
 */
public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(Connection connection);

    SqlSession openSession(TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType);

    SqlSession openSession(ExecutorType execType, boolean autoCommit);

    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType, Connection connection);

    Configuration getConfiguration();
}
