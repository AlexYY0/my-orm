package club.emperorws.orm.result;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.ResultMap;
import club.emperorws.orm.mapping.ResultMapping;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.reflection.MetaClass;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.ReflectorFactory;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.type.TypeHandler;
import club.emperorws.orm.type.TypeHandlerRegistry;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 默认的ResultSetHandler
 *
 * @author: EmperorWS
 * @date: 2023/4/28 15:22
 * @description: DefaultResultSetHandler: 默认的ResultSetHandler
 */
public class DefaultResultSetHandler implements ResultSetHandler {

    private final Configuration configuration;

    private final MappedStatement mappedStatement;

    private final RowBounds rowBounds;

    private final ResultHandler<?> resultHandler;

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final ObjectFactory objectFactory;

    private final ReflectorFactory reflectorFactory;

    /**
     * 自动映射相关信息缓存
     */
    private final Map<String, List<UnMappedColumnAutoMapping>> autoMappingsCache = new HashMap<>();

    public DefaultResultSetHandler(MappedStatement mappedStatement, ResultHandler<?> resultHandler, RowBounds rowBounds) {
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.objectFactory = configuration.getObjectFactory();
        this.reflectorFactory = configuration.getReflectorFactory();
        this.resultHandler = resultHandler;
    }

    /**
     * 结果集处理
     *
     * @param stmt Statement执行sql
     * @return 执行结果
     * @throws SQLException 异常
     */
    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {
        //结果集存储，现阶段没有存储过程，size为1
        final List<Object> multipleResults = new ArrayList<>();
        //获取SQL的返回结果
        ResultSetWrapper rsw = getFirstResultSet(stmt);
        if (rsw != null) {
            //获取返回结果信息
            ResultMap resultMap = mappedStatement.getResultMap();
            //处理查询结果
            handleResultSet(rsw, resultMap, multipleResults);
        }
        //返回结果集
        return collapseSingleResultList(multipleResults);
    }

    /**
     * 流式查询游标结果集处理，ignore
     *
     * @param stmt Statement执行sql
     */
    @Override
    public <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException {
        return null;
    }

    /**
     * 存储过程结果集处理，ignore
     *
     * @param cs CallableStatement存储过程执行sql
     */
    @Override
    public void handleOutputParameters(CallableStatement cs) throws SQLException {
    }

    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults) throws SQLException {
        try {
            if (resultHandler == null) {
                //结果行存储处理
                DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
                handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds);
                multipleResults.add(defaultResultHandler.getResultList());
            } else {
                handleRowValues(rsw, resultMap, resultHandler, rowBounds);
            }
        } finally {
            closeResultSet(rsw.getResultSet());
        }
    }

    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds) throws SQLException {
        //if嵌套查询：暂不写复杂查询
        //else简单查询
        handleRowValuesForSimpleResultMap(rsw, resultMap, resultHandler, rowBounds);
    }

    private void handleRowValuesForSimpleResultMap(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds) throws SQLException {
        //结果上下文存储器
        DefaultResultContext<Object> resultContext = new DefaultResultContext<>();
        //获取sql查询结果
        ResultSet resultSet = rsw.getResultSet();
        //内存分页
        skipRows(resultSet, rowBounds);
        //循环遍历结果集记录，并存储至resultContext
        while (shouldProcessMoreRows(resultContext, rowBounds) && !resultSet.isClosed() && resultSet.next()) {
            //获取记录的映射对象
            Object rowValue = getRowValue(rsw, resultMap);
            storeObject(resultHandler, resultContext, rowValue, resultSet);
        }
    }

    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        //创建一个空对象
        Object rowValue = createResultObject(rsw, resultMap);
        //给空对象set值
        if (rowValue != null && !hasTypeHandlerForResultObject(rsw, resultMap.getType())) {
            final MetaObject metaObject = configuration.newMetaObject(rowValue);
            boolean foundValues = false;
            if (shouldApplyAutomaticMappings(resultMap, false)) {
                //未定义的字段-->自动映射
                foundValues = applyAutomaticMappings(rsw, resultMap, metaObject);
            }
            //真正的结果集映射，设置rowValue的属性
            foundValues = applyPropertyMappings(rsw, resultMap, metaObject) || foundValues;
            //如果所有列都为空，返回空实例还是null
            rowValue = foundValues || configuration.isReturnInstanceForEmptyRow() ? rowValue : null;
        }
        return rowValue;
    }

    /**
     * 通过构造器的方式创建一个空对象（忽略通过构造器的方式创建对象，没有实现！！！）
     *
     * @param rsw       ResultSet
     * @param resultMap 结果映射
     * @return 创建的对象
     * @throws SQLException 异常
     */
    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        final Class<?> resultType = resultMap.getType();
        final MetaClass metaType = MetaClass.forClass(resultType, reflectorFactory);
        if (hasTypeHandlerForResultObject(rsw, resultType)) {
            //目测resultType是基本类型时（初始化就注册的TypeHandler），才会为true，且返回结果只有一列
            return createPrimitiveResultObject(rsw, resultMap);
        } else if (resultType.isInterface() || metaType.hasDefaultConstructor()) {
            return objectFactory.create(resultType);
        }
        throw new OrmException("Do not know how to create an instance of " + resultType);
    }

    /**
     * 创建一个对象：返回结果只有一列，且是基本类型（初始化就注册的TypeHandler），直接获取数据
     *
     * @param rsw       ResultSet
     * @param resultMap 结果映射
     * @return 创建的对象
     * @throws SQLException 异常
     */
    private Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
        final Class<?> resultType = resultMap.getType();
        final String columnName;
        if (!resultMap.getResultMappings().isEmpty()) {
            final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
            final ResultMapping mapping = resultMappingList.get(0);
            columnName = mapping.getColumn();
        } else {
            columnName = rsw.getColumnNames().get(0);
        }
        final TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
        return typeHandler.getResult(rsw.getResultSet(), columnName);
    }

    /**
     * 自动映射未定义的属性
     *
     * @param rsw        ResultSet
     * @param resultMap  结果值映射信息
     * @param metaObject 元对象
     * @return 是否自动映射成功
     * @throws SQLException 异常
     */
    private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) throws SQLException {
        List<UnMappedColumnAutoMapping> autoMapping = createAutomaticMappings(rsw, resultMap, metaObject);
        boolean foundValues = false;
        if (!autoMapping.isEmpty()) {
            for (UnMappedColumnAutoMapping mapping : autoMapping) {
                final Object value = mapping.typeHandler.getResult(rsw.getResultSet(), mapping.column);
                if (value != null) {
                    foundValues = true;
                }
                if (value != null || (configuration.isCallSettersOnNulls() && !mapping.primitive)) {
                    metaObject.setValue(mapping.property, value);
                }
            }
        }
        return foundValues;
    }

    /**
     * 获取未定义的属性映射
     *
     * @param rsw        ResultSet
     * @param resultMap  结果值映射信息
     * @param metaObject 元对象
     * @return 未定义的属性映射
     * @throws SQLException 异常
     */
    private List<UnMappedColumnAutoMapping> createAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) throws SQLException {
        final String mapKey = resultMap.getId();
        List<UnMappedColumnAutoMapping> autoMapping = autoMappingsCache.get(mapKey);
        if (autoMapping == null) {
            autoMapping = new ArrayList<>();
            final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap);
            for (String columnName : unmappedColumnNames) {
                String propertyName = columnName;
                final String property = metaObject.findProperty(propertyName, configuration.isMapUnderscoreToCamelCase());
                if (property != null && metaObject.hasSetter(property)) {
                    //映射已存在
                    if (resultMap.getMappedProperties().contains(property)) {
                        continue;
                    }
                    //映射不存在
                    final Class<?> propertyType = metaObject.getSetterType(property);
                    if (typeHandlerRegistry.hasTypeHandler(propertyType, rsw.getJdbcType(columnName))) {
                        final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
                        autoMapping.add(new UnMappedColumnAutoMapping(columnName, property, typeHandler, propertyType.isPrimitive()));
                    }
                }
            }
            autoMappingsCache.put(mapKey, autoMapping);
        }
        return autoMapping;
    }

    /**
     * 结果集映射，设置值
     *
     * @param rsw        结果集
     * @param resultMap  映射信息
     * @param metaObject 元对象
     * @return 是否映射成功
     * @throws SQLException 异常
     */
    private boolean applyPropertyMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) throws SQLException {
        final List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap);
        boolean foundValues = false;
        final List<ResultMapping> propertyMappings = resultMap.getResultMappings();
        for (ResultMapping propertyMapping : propertyMappings) {
            String column = propertyMapping.getColumn();
            if (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) {
                //获取记录列的值
                Object value = getPropertyMappingValue(rsw.getResultSet(), propertyMapping);
                final String property = propertyMapping.getProperty();
                if (property == null) {
                    continue;
                }
                if (value != null) {
                    foundValues = true;
                }
                if (value != null || (configuration.isCallSettersOnNulls() && !metaObject.getSetterType(property).isPrimitive())) {
                    metaObject.setValue(property, value);
                }
            }
        }
        return foundValues;
    }

    /**
     * 获取记录列的值
     *
     * @param rs              ResultSet
     * @param propertyMapping 列名
     * @return 记录列的值
     * @throws SQLException 异常
     */
    private Object getPropertyMappingValue(ResultSet rs, ResultMapping propertyMapping) throws SQLException {
        final TypeHandler<?> typeHandler = propertyMapping.getTypeHandler();
        final String column = propertyMapping.getColumn();
        return typeHandler.getResult(rs, column);
    }

    /**
     * 返回SQL执行结果，一般用此方法即可
     *
     * @param stmt SQL执行器Statement
     * @return SQL执行结果
     * @throws SQLException 异常
     */
    private ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            // move forward to get the first resultset in case the driver
            // doesn't return the resultset as the first result (HSQLDB 2.1)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    // no more results. Must be no resultset
                    break;
                }
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    /**
     * 返回SQL执行结果多结果集，一般存储过程会使用到此方法，一般不会用此方法
     * <p>ignore</p>
     *
     * @param stmt SQL执行器Statement
     * @return SQL执行结果
     */
    private ResultSetWrapper getNextResultSet(Statement stmt) {
        // Making this method tolerant of bad JDBC drivers
        try {
            if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
                // Crazy Standard JDBC way of determining if there are more results
                if (!(!stmt.getMoreResults() && stmt.getUpdateCount() == -1)) {
                    ResultSet rs = stmt.getResultSet();
                    if (rs == null) {
                        return getNextResultSet(stmt);
                    } else {
                        return new ResultSetWrapper(rs, configuration);
                    }
                }
            }
        } catch (Exception e) {
            // Intentionally ignored.
        }
        return null;
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> collapseSingleResultList(List<Object> multipleResults) {
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }

    /**
     * 内存分页
     *
     * @param rs        查询结果
     * @param rowBounds 分页信息
     * @throws SQLException 异常
     */
    private void skipRows(ResultSet rs, RowBounds rowBounds) throws SQLException {
        if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            if (rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET) {
                rs.absolute(rowBounds.getOffset());
            }
        } else {
            for (int i = 0; i < rowBounds.getOffset(); i++) {
                if (!rs.next()) {
                    break;
                }
            }
        }
    }

    /**
     * 是否继续读取记录
     *
     * @param context   已存储的记录
     * @param rowBounds 分页信息
     * @return 是否继续读取记录
     */
    private boolean shouldProcessMoreRows(ResultContext<?> context, RowBounds rowBounds) {
        return !context.isStopped() && context.getResultCount() < rowBounds.getLimit();
    }

    /**
     * 返回结果是否有对应的TypeHandler
     * <p>目测resultType是基本类型时（初始化就注册的TypeHandler），才会为true，且返回结果只有一列</p>
     *
     * @param rsw        ResultSet的包装器
     * @param resultType 返回结果的class类型
     * @return 返回结果是否有对应的TypeHandler
     */
    private boolean hasTypeHandlerForResultObject(ResultSetWrapper rsw, Class<?> resultType) {
        if (rsw.getColumnNames().size() == 1) {
            return typeHandlerRegistry.hasTypeHandler(resultType, rsw.getJdbcType(rsw.getColumnNames().get(0)));
        }
        return typeHandlerRegistry.hasTypeHandler(resultType);
    }

    /**
     * 是否开启自动映射
     *
     * @param resultMap 返回结果的映射方式
     * @param isNested  嵌套结果是否需要映射
     * @return 是否开启自动映射
     */
    private boolean shouldApplyAutomaticMappings(ResultMap resultMap, boolean isNested) {
        if (resultMap.getAutoMapping() != null) {
            return resultMap.getAutoMapping();
        } else {
            if (isNested) {
                return AutoMappingBehavior.FULL == configuration.getAutoMappingBehavior();
            } else {
                return AutoMappingBehavior.NONE != configuration.getAutoMappingBehavior();
            }
        }
    }

    private void storeObject(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue, ResultSet rs) throws SQLException {
        //if嵌套结果集：暂不写复杂结果集
        //else简单结果集
        callResultHandler(resultHandler, resultContext, rowValue);
    }

    @SuppressWarnings("unchecked" /* because ResultHandler<?> is always ResultHandler<Object>*/)
    private void callResultHandler(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue) {
        resultContext.nextResultObject(rowValue);
        //记录行存储
        ((ResultHandler<Object>) resultHandler).handleResult(resultContext);
    }

    /*************************************************inner class*********************************************************/

    private static class UnMappedColumnAutoMapping {
        private final String column;
        private final String property;
        private final TypeHandler<?> typeHandler;
        private final boolean primitive;

        public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
            this.column = column;
            this.property = property;
            this.typeHandler = typeHandler;
            this.primitive = primitive;
        }
    }
}
