package club.emperorws.orm.mapping;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeHandler;
import club.emperorws.orm.type.TypeHandlerRegistry;

/**
 * 一个sql请求参数的装载体
 *
 * @author: EmperorWS
 * @date: 2023/4/23 17:41
 * @description: ParameterMapping: 一个sql请求参数的装载体
 */
public class ParameterMapping {

    private Configuration configuration;

    /**
     * 参数名称
     */
    private String property;

    /**
     * 参数映射的Java类型（目测看起来用处不大，调用存储过程会用到，但是没有实现存储过程的执行）
     */
    private Class<?> javaType = Object.class;

    /**
     * 参数映射的JDBC数据库类型
     */
    private JdbcType jdbcType;

    /**
     * 参数对应的类型处理器
     */
    private TypeHandler<?> typeHandler;

    private ParameterMapping() {
    }

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property, TypeHandler<?> typeHandler) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.typeHandler = typeHandler;
        }

        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            parameterMapping.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapping build() {
            resolveTypeHandler();
            validate();
            return parameterMapping;
        }

        private void validate() {
            if (parameterMapping.typeHandler == null) {
                throw new IllegalStateException("Type handler was null on parameter mapping for property '"
                        + parameterMapping.property + "'. It was either not specified and/or could not be found for the javaType ("
                        + parameterMapping.javaType.getName() + ") : jdbcType (" + parameterMapping.jdbcType + ") combination.");
            }
        }

        private void resolveTypeHandler() {
            if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
            }
        }
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParameterMapping{");
        //sb.append("configuration=").append(configuration); // configuration doesn't have a useful .toString()
        sb.append("property='").append(property).append('\'');
        sb.append(", javaType=").append(javaType);
        sb.append(", jdbcType=").append(jdbcType);
        //sb.append(", typeHandler=").append(typeHandler); // typeHandler also doesn't have a useful .toString()
        sb.append('}');
        return sb.toString();
    }
}
