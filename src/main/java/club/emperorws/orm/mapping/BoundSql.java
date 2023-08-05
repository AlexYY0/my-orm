package club.emperorws.orm.mapping;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.bingding.MapperMethod;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态解析后的可执行SQL语句对象
 *
 * @author: EmperorWS
 * @date: 2023/4/23 17:36
 * @description: BoundSql: 动态解析后的可执行SQL语句对象
 */
public class BoundSql {

    /**
     * 解析完成的动态预编译可执行sql，#{}、${}已被解析
     */
    private final String sql;

    /**
     * sql参数的相关信息
     */
    private final List<ParameterMapping> parameterMappings;

    /**
     * 封装的请求参数集合，实际是一个{@link MapperMethod.ParamMap}
     */
    private final Object parameterObject;

    /**
     * 额外的参数集合：运行过程中产生的额外临时的参数，方便sql获取数据
     */
    private final Map<String, Object> additionalParameters;

    /**
     * additionalParameters的元对象封装，方便后续的get、set
     */
    private final MetaObject metaParameters;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterObject = parameterObject;
        this.additionalParameters = new HashMap<>();
        this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public boolean hasAdditionalParameter(String name) {
        String paramName = new PropertyTokenizer(name).getName();
        return additionalParameters.containsKey(paramName);
    }

    public void setAdditionalParameter(String name, Object value) {
        metaParameters.setValue(name, value);
    }

    public Object getAdditionalParameter(String name) {
        return metaParameters.getValue(name);
    }
}
