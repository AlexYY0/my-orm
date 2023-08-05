package club.emperorws.orm.parameter;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.bingding.MapperMethod;
import club.emperorws.orm.exception.ErrorContext;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.BoundSql;
import club.emperorws.orm.mapping.MappedStatement;
import club.emperorws.orm.mapping.ParameterMapping;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeHandler;
import club.emperorws.orm.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * 默认的请求参数处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/27 17:15
 * @description: DefaultParameterHandler: 默认的请求参数处理器
 */
public class DefaultParameterHandler implements ParameterHandler {

    /**
     * 一条sql语句的装载体
     */
    private final MappedStatement mappedStatement;

    /**
     * 单例的配置类
     */
    private final Configuration configuration;

    /**
     * 类型处理器的注册集合
     */
    private final TypeHandlerRegistry typeHandlerRegistry;

    /**
     * sql语句对象
     */
    private final BoundSql boundSql;

    /**
     * 封装的请求参数对象集合{@link MapperMethod.ParamMap}
     */
    private final Object parameterObject;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            //循环取出并设置PreparedStatement的参数
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                //todo 暂时不想管存储过程的执行
                //参数对应的值
                Object value;
                //参数的名称
                String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    //额外的参数，可以直接获取
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    //hasTypeHandler会自动跳过ParamMap（ps：目测永远不会走这里）
                    value = parameterObject;
                } else {
                    //一般都是进入这里
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = configuration.getJdbcTypeForNull();
                }
                try {
                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                } catch (Exception e) {
                    throw new OrmException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                }
            }
        }
    }
}
