package club.emperorws.orm.session.managed;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.session.ExecutorType;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.session.SqlSessionFactory;
import club.emperorws.orm.session.TransactionIsolationLevel;
import club.emperorws.orm.session.defaults.DefaultSqlSessionFactory;
import club.emperorws.orm.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 外部管控型SqlSession
 *
 * @author: EmperorWS
 * @date: 2023/5/10 16:25
 * @description: SqlSessionManager: 外部管控型SqlSession
 */
public class SqlSessionManager implements SqlSessionFactory, SqlSession {

    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSession sqlSessionProxy;

    private final ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<>();

    private SqlSessionManager(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor());
    }

    public static SqlSessionManager newInstance(Configuration config) {
        return new SqlSessionManager(new DefaultSqlSessionFactory(config));
    }

    public void startManagedSession() {
        this.localSqlSession.set(openSession());
    }

    public void startManagedSession(boolean autoCommit) {
        this.localSqlSession.set(openSession(autoCommit));
    }

    public void startManagedSession(Connection connection) {
        this.localSqlSession.set(openSession(connection));
    }

    public void startManagedSession(TransactionIsolationLevel level) {
        this.localSqlSession.set(openSession(level));
    }

    public void startManagedSession(ExecutorType execType) {
        this.localSqlSession.set(openSession(execType));
    }

    public void startManagedSession(ExecutorType execType, boolean autoCommit) {
        this.localSqlSession.set(openSession(execType, autoCommit));
    }

    public void startManagedSession(ExecutorType execType, TransactionIsolationLevel level) {
        this.localSqlSession.set(openSession(execType, level));
    }

    public void startManagedSession(ExecutorType execType, Connection connection) {
        this.localSqlSession.set(openSession(execType, connection));
    }

    public boolean isManagedSessionStarted() {
        return this.localSqlSession.get() != null;
    }

    @Override
    public SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return sqlSessionFactory.openSession(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return sqlSessionFactory.openSession(level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return sqlSessionFactory.openSession(execType);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return sqlSessionFactory.openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return sqlSessionFactory.openSession(execType, level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return sqlSessionFactory.openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return sqlSessionFactory.getConfiguration();
    }

    @Override
    public <T> T selectOne(SqlSource sqlSource) {
        return sqlSessionProxy.selectOne(sqlSource);
    }

    @Override
    public <T> T selectOne(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.selectOne(sqlSource, parameter);
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, String mapKey) {
        return sqlSessionProxy.selectMap(sqlSource, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey) {
        return sqlSessionProxy.selectMap(sqlSource, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey, RowBounds rowBounds) {
        return sqlSessionProxy.selectMap(sqlSource, parameter, mapKey, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource) {
        return sqlSessionProxy.selectCursor(sqlSource);
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.selectCursor(sqlSource, parameter);
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        return sqlSessionProxy.selectCursor(sqlSource, parameter, rowBounds);
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource) {
        return sqlSessionProxy.selectList(sqlSource);
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.selectList(sqlSource, parameter);
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        return sqlSessionProxy.selectList(sqlSource, parameter, rowBounds);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, ResultHandler handler) {
        sqlSessionProxy.select(sqlSource, handler);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, ResultHandler handler) {
        sqlSessionProxy.select(sqlSource, parameter, handler);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        sqlSessionProxy.select(sqlSource, parameter, rowBounds, handler);
    }

    @Override
    public int insert(SqlSource sqlSource) {
        return sqlSessionProxy.insert(sqlSource);
    }

    @Override
    public int insert(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.insert(sqlSource, parameter);
    }

    @Override
    public int update(SqlSource sqlSource) {
        return sqlSessionProxy.update(sqlSource);
    }

    @Override
    public int update(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.update(sqlSource, parameter);
    }

    @Override
    public int delete(SqlSource sqlSource) {
        return sqlSessionProxy.delete(sqlSource);
    }

    @Override
    public int delete(SqlSource sqlSource, Object parameter) {
        return sqlSessionProxy.delete(sqlSource, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot get connection.  No managed session is started.");
        }
        return sqlSession.getConnection();
    }

    @Override
    public void commit() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit();
    }

    @Override
    public void commit(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot commit.  No managed session is started.");
        }
        sqlSession.commit(force);
    }

    @Override
    public void rollback() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback();
    }

    @Override
    public void rollback(boolean force) {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot rollback.  No managed session is started.");
        }
        sqlSession.rollback(force);
    }

    @Override
    public List<BatchResult> flushStatements() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot rollback.  No managed session is started.");
        }
        return sqlSession.flushStatements();
    }

    @Override
    public void close() {
        final SqlSession sqlSession = localSqlSession.get();
        if (sqlSession == null) {
            throw new OrmException("Error:  Cannot close.  No managed session is started.");
        }
        try {
            sqlSession.close();
        } finally {
            localSqlSession.remove();
        }
    }

    private class SqlSessionInterceptor implements InvocationHandler {
        public SqlSessionInterceptor() {
            // Prevent Synthetic Access
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final SqlSession sqlSession = SqlSessionManager.this.localSqlSession.get();
            if (sqlSession != null) {
                try {
                    return method.invoke(sqlSession, args);
                } catch (Throwable t) {
                    throw ExceptionUtil.unwrapThrowable(t);
                }
            } else {
                try (SqlSession autoSqlSession = openSession()) {
                    try {
                        final Object result = method.invoke(autoSqlSession, args);
                        autoSqlSession.commit();
                        return result;
                    } catch (Throwable t) {
                        autoSqlSession.rollback();
                        throw ExceptionUtil.unwrapThrowable(t);
                    }
                }
            }
        }
    }
}
