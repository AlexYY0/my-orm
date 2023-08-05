package club.emperorws.orm.type;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.io.Resources;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 空/null的类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/20 14:19
 * @description: UnknownTypeHandler: 空/null的类型处理器
 */
public class UnknownTypeHandler extends BaseTypeHandler<Object> {

    /**
     * 实在没办法，就用ObjectTypeHandler处理数据
     */
    private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

    private final Configuration config;

    /**
     * 提供一个用于获取TypeHandler注册器的函数
     */
    private final Supplier<TypeHandlerRegistry> typeHandlerRegistrySupplier;

    public UnknownTypeHandler(Configuration configuration) {
        this.config = configuration;
        this.typeHandlerRegistrySupplier = configuration::getTypeHandlerRegistry;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        //获取TypeHandler
        TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
        //设置预编译的sql请求参数
        handler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
        return handler.getResult(rs, columnName);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(rs.getMetaData(), columnIndex);
        if (handler == null || handler instanceof UnknownTypeHandler) {
            handler = OBJECT_TYPE_HANDLER;
        }
        return handler.getResult(rs, columnIndex);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getObject(columnIndex);
    }

    private TypeHandler<?> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
        TypeHandler<?> handler;
        if (parameter == null) {
            handler = OBJECT_TYPE_HANDLER;
        } else {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(parameter.getClass(), jdbcType);
            // check if handler is null
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
        }
        return handler;
    }

    private TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
        try {
            Map<String,Integer> columnIndexLookup;
            columnIndexLookup = new HashMap<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            boolean useColumnLabel = config.isUseColumnLabel();
            for (int i = 1; i <= count; i++) {
                String name = useColumnLabel ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
                columnIndexLookup.put(name,i);
            }
            Integer columnIndex = columnIndexLookup.get(column);
            TypeHandler<?> handler = null;
            if (columnIndex != null) {
                handler = resolveTypeHandler(rsmd, columnIndex);
            }
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
            return handler;
        } catch (SQLException e) {
            throw new OrmException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
        }
    }

    private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) {
        TypeHandler<?> handler = null;
        JdbcType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
        Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
        if (javaType != null && jdbcType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(javaType, jdbcType);
        } else if (javaType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(javaType);
        } else if (jdbcType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(jdbcType);
        }
        return handler;
    }

    /**
     * 根据列index获取JDBC的Type
     */
    private JdbcType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return JdbcType.forCode(rsmd.getColumnType(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }

    private Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return Resources.classForName(rsmd.getColumnClassName(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }
}
