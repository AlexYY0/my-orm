package club.emperorws.orm.session.defaults;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.ErrorContext;
import club.emperorws.orm.exception.ExceptionFactory;
import club.emperorws.orm.exception.TooManyResultsException;
import club.emperorws.orm.executor.Executor;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.reflection.ParamNameResolver;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.DefaultMapResultHandler;
import club.emperorws.orm.result.DefaultResultContext;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.session.SqlSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认的SqlSession
 *
 * @author: EmperorWS
 * @date: 2023/5/9 16:45
 * @description: DefaultSqlSession: 默认的SqlSession
 */
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    private final boolean autoCommit;
    private List<Cursor<?>> cursorList;

    public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit = autoCommit;
    }

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this(configuration, executor, false);
    }

    @Override
    public <T> T selectOne(SqlSource sqlSource) {
        return this.selectOne(sqlSource, null);
    }

    @Override
    public <T> T selectOne(SqlSource sqlSource, Object parameter) {
        List<T> list = this.selectList(sqlSource, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, String mapKey) {
        return this.selectMap(sqlSource, null, mapKey, RowBounds.DEFAULT);
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey) {
        return this.selectMap(sqlSource, parameter, mapKey, RowBounds.DEFAULT);
    }

    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey, RowBounds rowBounds) {
        final List<? extends V> list = selectList(sqlSource, parameter, rowBounds);
        //List转Map
        final DefaultMapResultHandler<K, V> mapResultHandler = new DefaultMapResultHandler<>(mapKey,
                configuration.getObjectFactory(), configuration.getObjectWrapperFactory(), configuration.getReflectorFactory());
        final DefaultResultContext<V> context = new DefaultResultContext<>();
        for (V o : list) {
            context.nextResultObject(o);
            mapResultHandler.handleResult(context);
        }
        return mapResultHandler.getMappedResults();
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource) {
        return selectCursor(sqlSource, null);
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter) {
        return selectCursor(sqlSource, parameter, RowBounds.DEFAULT);
    }

    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        try {
            MappedStatement ms = configuration.getMappedStatement(sqlSource);
            Cursor<T> cursor = executor.queryCursor(ms, wrapCollection(parameter), rowBounds);
            registerCursor(cursor);
            return cursor;
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource) {
        return this.selectList(sqlSource, null);
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter) {
        return this.selectList(sqlSource, parameter, RowBounds.DEFAULT);
    }

    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        return selectList(sqlSource, parameter, rowBounds, Executor.NO_RESULT_HANDLER);
    }

    @SuppressWarnings("rawtypes")
    private <E> List<E> selectList(SqlSource sqlSource, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        try {
            MappedStatement ms = configuration.getMappedStatement(sqlSource);
            return executor.query(ms, wrapCollection(parameter), rowBounds, handler);
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error querying database.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, ResultHandler handler) {
        select(sqlSource, parameter, RowBounds.DEFAULT, handler);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, ResultHandler handler) {
        select(sqlSource, null, RowBounds.DEFAULT, handler);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        selectList(sqlSource, parameter, rowBounds, handler);
    }

    @Override
    public int insert(SqlSource sqlSource) {
        return insert(sqlSource, null);
    }

    @Override
    public int insert(SqlSource sqlSource, Object parameter) {
        return update(sqlSource, parameter);
    }

    @Override
    public int update(SqlSource sqlSource) {
        return update(sqlSource, null);
    }

    @Override
    public int update(SqlSource sqlSource, Object parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(sqlSource);
            return executor.update(ms, wrapCollection(parameter));
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public int delete(SqlSource sqlSource) {
        return update(sqlSource, null);
    }

    @Override
    public int delete(SqlSource sqlSource, Object parameter) {
        return update(sqlSource, parameter);
    }

    @Override
    public void commit() {
        commit(false);
    }

    @Override
    public void commit(boolean force) {
        try {
            executor.commit(isCommitOrRollbackRequired(force));
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void rollback() {
        rollback(false);
    }

    @Override
    public void rollback(boolean force) {
        try {
            executor.rollback(isCommitOrRollbackRequired(force));
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error rolling back transaction.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public List<BatchResult> flushStatements() {
        try {
            return executor.flushStatements();
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error flushing sqlSources.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void close() {
        try {
            executor.close(isCommitOrRollbackRequired(false));
            closeCursors();
        } finally {
            ErrorContext.instance().reset();
        }
    }

    private void closeCursors() {
        if (cursorList != null && !cursorList.isEmpty()) {
            for (Cursor<?> cursor : cursorList) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    throw ExceptionFactory.wrapException("Error closing cursor.  Cause: " + e, e);
                }
            }
            cursorList.clear();
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Connection getConnection() {
        try {
            return executor.getTransaction().getConnection();
        } catch (SQLException e) {
            throw ExceptionFactory.wrapException("Error getting a new connection.  Cause: " + e, e);
        }
    }

    private <T> void registerCursor(Cursor<T> cursor) {
        if (cursorList == null) {
            cursorList = new ArrayList<>();
        }
        cursorList.add(cursor);
    }

    private boolean isCommitOrRollbackRequired(boolean force) {
        return !autoCommit || force;
    }

    private Object wrapCollection(final Object object) {
        return ParamNameResolver.wrapToMapIfCollection(object, null);
    }
}
