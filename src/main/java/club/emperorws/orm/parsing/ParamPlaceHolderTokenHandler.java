package club.emperorws.orm.parsing;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.ParameterMapping;
import club.emperorws.orm.reflection.MetaClass;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeHandlerRegistry;
import club.emperorws.orm.util.ArrayUtil;
import club.emperorws.orm.util.BuilderUtil;
import club.emperorws.orm.util.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * #{}请求参数替换为?拼接到SQL的解析器
 *
 * @author: EmperorWS
 * @date: 2023/5/6 16:47
 * @description: ParamPlaceHolderTokenHandler: #{}请求参数替换为?拼接到SQL的解析器
 */
public class ParamPlaceHolderTokenHandler implements TokenHandler {

    private static final String PARAMETER_PROPERTIES = "javaType,jdbcType,resultMap,typeHandler";

    private static final Pattern SQL_REPLACE_PATTERN = Pattern.compile("#\\{.+?}");

    private final Configuration configuration;

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final BuilderUtil builderUtil;

    /**
     * 请求参数的类型
     * <p>其实是封装后的参数的类型，为：ParamMap</p>
     */
    private final Class<?> parameterType;

    /**
     * 封装后ParamMap/单个对象-->再封装后ContextMap的请求参数元对象
     */
    private final MetaObject metaParameters;

    /**
     * 动态请求参数的存储
     * <p>#{}里的内容</p>
     */
    private final List<ParameterMapping> parameterMappingList = new ArrayList<>();

    public ParamPlaceHolderTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
        this.configuration = configuration;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.builderUtil = configuration.getBuilderUtil();
        this.parameterType = parameterType;
        this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    /**
     * 动态sql语句解析
     *
     * @param dynamicSql 原sql语句
     * @return 解析后的sql语句
     */
    @Override
    public String handleToken(String dynamicSql) {
        StringBuffer sqlStr = new StringBuffer();
        Matcher matcher = SQL_REPLACE_PATTERN.matcher(dynamicSql);
        while (matcher.find()) {
            String sqlSegment = matcher.group()
                    .replace("#{", "")
                    .replace("}", "");
            parameterMappingList.add(buildParameterMapping(sqlSegment));
            matcher.appendReplacement(sqlStr, "?");
        }
        matcher.appendTail(sqlStr);
        //返回预编译的SQL
        return sqlStr.toString();
    }

    /**
     * 获取#{}里面的，并映射为ParameterMapping
     *
     * @param content #{}里面的内容
     * @return #{}里面的内容映射为ParameterMapping
     */
    private ParameterMapping buildParameterMapping(String content) {
        //解析content
        Map<String, String> propertiesMap = parseParameterMapping(content);
        String property = propertiesMap.get("property");
        //先根据属性名称，获取JavaType
        Class<?> propertyType;
        if (metaParameters.hasGetter(property)) {
            propertyType = metaParameters.getGetterType(property);
        } else if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
            propertyType = parameterType;
        } else if (JdbcType.CURSOR.name().equals(propertiesMap.get("jdbcType"))) {
            propertyType = java.sql.ResultSet.class;
        } else if (property == null || Map.class.isAssignableFrom(parameterType)) {
            propertyType = Object.class;
        } else {
            MetaClass metaClass = MetaClass.forClass(parameterType, configuration.getReflectorFactory());
            if (metaClass.hasGetter(property)) {
                propertyType = metaClass.getGetterType(property);
            } else {
                propertyType = Object.class;
            }
        }
        ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
        //根据解析出来的值设置属性
        Class<?> javaType = propertyType;
        String typeHandlerAlias = null;
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if ("javaType".equals(name)) {
                javaType = builderUtil.resolveClass(value);
                builder.javaType(javaType);
            } else if ("jdbcType".equals(name)) {
                builder.jdbcType(builderUtil.resolveJdbcType(value));
            } else if ("typeHandler".equals(name)) {
                typeHandlerAlias = value;
            } else if ("property".equals(name)) {
                // Do Nothing
            } else if ("expression".equals(name)) {
                throw new OrmException("Expression based parameters are not supported yet");
            } else {
                throw new OrmException("An invalid property '" + name + "' was found in mapping #{" + content + "}.  Valid properties are " + PARAMETER_PROPERTIES);
            }
        }
        if (typeHandlerAlias != null) {
            builder.typeHandler(builderUtil.resolveTypeHandler(javaType, typeHandlerAlias));
        }
        return builder.build();
    }

    /**
     * sql请求参数#{}解析
     *
     * @param content sql请求参数#{}
     * @return 解析后转为map
     */
    private Map<String, String> parseParameterMapping(String content) {
        Map<String, String> propertiesMap = new HashMap<>();
        String[] pairs = content.split(",");
        //第一个必定为属性名称
        propertiesMap.put("property", pairs[0]);
        //剩下的为其他属性
        for (int i = 1; i < pairs.length; i++) {
            String[] keyValue = pairs[i].split("=");
            if (ArrayUtil.isNotEmpty(keyValue)) {
                propertiesMap.put(keyValue[0], keyValue[1]);
            }
        }
        return propertiesMap;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }
}
