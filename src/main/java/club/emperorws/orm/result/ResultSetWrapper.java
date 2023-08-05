package club.emperorws.orm.result;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.io.Resources;
import club.emperorws.orm.mapping.ResultMap;
import club.emperorws.orm.type.*;
import club.emperorws.orm.util.CollectionUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * ResultSet结果集的包装器
 *
 * @author: EmperorWS
 * @date: 2023/4/28 17:27
 * @description: ResultSetWrapper: ResultSet结果集的包装器
 */
public class ResultSetWrapper {

    /**
     * 结果集
     */
    private final ResultSet resultSet;

    /**
     * 类型处理器的注册器
     */
    private final TypeHandlerRegistry typeHandlerRegistry;

    /**
     * 查询结果列名集合
     */
    private final List<String> columnNames = new ArrayList<>();

    /**
     * 查询结果列对应的class类型名称
     */
    private final List<String> classNames = new ArrayList<>();

    /**
     * 查询结果列对应的JDBC的类型集合
     */
    private final List<JdbcType> jdbcTypes = new ArrayList<>();

    /**
     * 列名--列的class类型--TypeHandler之间的映射关系集合
     */
    private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<>();

    /**
     * 已指定的结果映射
     */
    private final List<String> mappedColumnNames = new ArrayList<>();

    /**
     * 未已指定的结果映射
     */
    private final List<String> unMappedColumnNames = new ArrayList<>();

    public ResultSetWrapper(ResultSet rs, Configuration configuration) throws SQLException {
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.resultSet = rs;
        final ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
            jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
            classNames.add(metaData.getColumnClassName(i));
        }
    }

    public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
        TypeHandler<?> handler = null;
        Map<Class<?>, TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
        if (columnHandlers == null) {
            columnHandlers = new HashMap<>();
            typeHandlerMap.put(columnName, columnHandlers);
        } else {
            handler = columnHandlers.get(propertyType);
        }
        //获取TypeHandler
        if (handler == null) {
            JdbcType jdbcType = getJdbcType(columnName);
            handler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
            // same：UnknownTypeHandler#resolveTypeHandler
            if (handler == null || handler instanceof UnknownTypeHandler) {
                final int index = columnNames.indexOf(columnName);
                final Class<?> javaType = resolveClass(classNames.get(index));
                if (javaType != null && jdbcType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
                } else if (javaType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(javaType);
                } else if (jdbcType != null) {
                    handler = typeHandlerRegistry.getTypeHandler(jdbcType);
                }
            }
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = new ObjectTypeHandler();
            }
            columnHandlers.put(propertyType, handler);
        }
        return handler;
    }

    private void loadMappedAndUnmappedColumnNames(ResultMap resultMap) throws SQLException {
        final Set<String> mappedColumns = resultMap.getMappedColumns();
        for (String columnName : columnNames) {
            final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
            if (mappedColumns.contains(upperColumnName)) {
                mappedColumnNames.add(upperColumnName);
            } else {
                unMappedColumnNames.add(columnName);
            }
        }
    }

    public List<String> getMappedColumnNames(ResultMap resultMap) throws SQLException {
        if (CollectionUtil.isEmpty(mappedColumnNames)) {
            loadMappedAndUnmappedColumnNames(resultMap);
        }
        return mappedColumnNames;
    }

    public List<String> getUnmappedColumnNames(ResultMap resultMap) throws SQLException {
        if (CollectionUtil.isEmpty(unMappedColumnNames)) {
            loadMappedAndUnmappedColumnNames(resultMap);
        }
        return unMappedColumnNames;
    }

    /**
     * 根据class name 获取Class
     *
     * @param className class name
     * @return class name对应的Class
     */
    private Class<?> resolveClass(String className) {
        try {
            if (className != null) {
                return Resources.classForName(className);
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return null;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<String> getClassNames() {
        return Collections.unmodifiableList(classNames);
    }

    public List<JdbcType> getJdbcTypes() {
        return jdbcTypes;
    }

    public JdbcType getJdbcType(String columnName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equalsIgnoreCase(columnName)) {
                return jdbcTypes.get(i);
            }
        }
        return null;
    }
}
