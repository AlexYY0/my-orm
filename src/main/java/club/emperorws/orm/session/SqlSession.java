package club.emperorws.orm.session;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 同一批次执行，共享同一个SqlSession
 *
 * @author: EmperorWS
 * @date: 2023/5/8 16:53
 * @description: SqlSession: 同一批次执行，共享同一个SqlSession
 */
public interface SqlSession extends Closeable {

    /**
     * 返回单行值的结果（没有请求参数）
     *
     * @param <T>       返回值类型
     * @param sqlSource 执行sql的语句
     * @return sql执行的单个返回结果
     */
    <T> T selectOne(SqlSource sqlSource);

    /**
     * 返回单行值的结果（有请求参数）
     *
     * @param <T>       返回值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return sql执行的单个返回结果
     */
    <T> T selectOne(SqlSource sqlSource, Object parameter);

    /**
     * 返回多行值的结果（没有请求参数）
     *
     * @param <E>       返回值类型
     * @param sqlSource 执行sql的语句
     * @return 返回List集合
     */
    <E> List<E> selectList(SqlSource sqlSource);

    /**
     * 返回多行值的结果（有请求参数）
     *
     * @param <E>       返回值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回List集合
     */
    <E> List<E> selectList(SqlSource sqlSource, Object parameter);

    /**
     * 返回多行值的结果（有请求参数、内存分页信息）
     *
     * @param <E>       返回值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds 内存分页信息
     * @return 返回List集合
     */
    <E> List<E> selectList(SqlSource sqlSource, Object parameter, RowBounds rowBounds);

    /**
     * 返回多行值的结果List--->转为Map（没有请求参数）
     * Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param sqlSource 执行sql的语句
     * @param mapKey    哪一个属性作为键
     * @return 返回map
     */
    <K, V> Map<K, V> selectMap(SqlSource sqlSource, String mapKey);

    /**
     * 返回多行值的结果List--->转为Map（有请求参数）
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param mapKey    哪一个属性作为键
     * @return 返回map
     */
    <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey);

    /**
     * 返回多行值的结果List--->转为Map（有请求参数、内存分页信息）
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param mapKey    哪一个属性作为键
     * @param rowBounds 内存分页信息
     * @return 返回map
     */
    <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey, RowBounds rowBounds);

    /**
     * 游标查询，暂不实现ignore
     *
     * @param <T>       返回值类型
     * @param sqlSource 执行sql的语句
     * @return 返回游标对象
     */
    <T> Cursor<T> selectCursor(SqlSource sqlSource);

    /**
     * 游标查询，暂不实现ignore
     *
     * @param <T>       返回值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回游标对象
     */
    <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter);

    /**
     * 游标查询，暂不实现ignore
     *
     * @param <T>       返回值类型
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds 内存分页信息
     * @return 返回游标对象
     */
    <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter, RowBounds rowBounds);

    /**
     * 没有返回值的查询
     * using a {@code ResultHandler}.
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param handler   ResultHandler每行结果的处理器
     */
    void select(SqlSource sqlSource, Object parameter, ResultHandler handler);

    /**
     * Retrieve a single row mapped from the statement
     * using a {@code ResultHandler}.
     *
     * @param sqlSource 执行sql的语句
     * @param handler   ResultHandler每行结果的处理器
     */
    void select(SqlSource sqlSource, ResultHandler handler);

    /**
     * 没有返回值的查询
     * using a {@code ResultHandler} and {@code RowBounds}.
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds RowBound instance to limit the query results
     * @param handler   ResultHandler每行结果的处理器
     */
    void select(SqlSource sqlSource, Object parameter, RowBounds rowBounds, ResultHandler handler);

    /**
     * 执行insert语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回insert执行受影响的行数
     */
    int insert(SqlSource sqlSource);

    /**
     * 执行insert语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回insert执行受影响的行数
     */
    int insert(SqlSource sqlSource, Object parameter);

    /**
     * 执行update语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回update执行受影响的行数
     */
    int update(SqlSource sqlSource);

    /**
     * 执行update语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回update执行受影响的行数
     */
    int update(SqlSource sqlSource, Object parameter);

    /**
     * 执行delete语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回delete执行受影响的行数
     */
    int delete(SqlSource sqlSource);

    /**
     * 执行update语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回delete执行受影响的行数
     */
    int delete(SqlSource sqlSource, Object parameter);

    /**
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     * To force the commit call {@link SqlSession#commit(boolean)}
     */
    void commit();

    /**
     * Flushes batch statements and commits database connection.
     *
     * @param force forces connection commit
     */
    void commit(boolean force);

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     * To force the rollback call {@link SqlSession#rollback(boolean)}
     */
    void rollback();

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     *
     * @param force forces connection rollback
     */
    void rollback(boolean force);

    /**
     * Flushes batch statements.
     *
     * @return BatchResult list of updated records
     */
    List<BatchResult> flushStatements();

    /**
     * Closes the session.
     */
    @Override
    void close();

    /**
     * 返回单例配置文件信息Configuration
     *
     * @return Configuration
     */
    Configuration getConfiguration();

    /**
     * 获取需要的Mapper对象
     *
     * @param <T>  Mapper的类型
     * @param type Mapper的class类型
     * @return Mapper对象
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    Connection getConnection();
}
