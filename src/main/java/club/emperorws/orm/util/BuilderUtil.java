package club.emperorws.orm.util;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.annotations.MapKey;
import club.emperorws.orm.annotations.ResultType;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.ResultSetType;
import club.emperorws.orm.reflection.MetaClass;
import club.emperorws.orm.reflection.TypeParameterResolver;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeAliasRegistry;
import club.emperorws.orm.type.TypeHandler;
import club.emperorws.orm.type.TypeHandlerRegistry;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 一些Builder过程中使用的工具
 *
 * @author: EmperorWS
 * @date: 2023/5/10 17:14
 * @description: BuilderUtil: 一些Builder过程中使用的工具
 */
public class BuilderUtil {

    private final Configuration configuration;
    private final TypeAliasRegistry typeAliasRegistry;
    private final TypeHandlerRegistry typeHandlerRegistry;

    public BuilderUtil(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Pattern parseExpression(String regex, String defaultValue) {
        return Pattern.compile(regex == null ? defaultValue : regex);
    }

    public Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    public Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    public Set<String> stringSetValueOf(String value, String defaultValue) {
        value = value == null ? defaultValue : value;
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    /**
     * 通过jdbcType别名，获取JdbcType
     *
     * @param alias jdbcType别名
     * @return JdbcType
     */
    public JdbcType resolveJdbcType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return JdbcType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new OrmException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    public ResultSetType resolveResultSetType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return ResultSetType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new OrmException("Error resolving ResultSetType. Cause: " + e, e);
        }
    }

    public Object createInstance(String alias) {
        Class<?> clazz = resolveClass(alias);
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OrmException("Error creating instance. Cause: " + e, e);
        }
    }

    /**
     * 通过javaType别名或真实的class String，获取javaType class
     *
     * @param alias javaType别名或真实的class String
     * @param <T>   类型
     * @return javaType class
     */
    public <T> Class<? extends T> resolveClass(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new OrmException("Error resolving class. Cause: " + e, e);
        }
    }

    /**
     * 通过typeHandler别名或真实的class String，获取typeHandler class
     *
     * @param javaType         对应的javaType类型
     * @param typeHandlerAlias typeHandler别名或真实的class String
     * @return typeHandler class
     */
    public TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
        if (typeHandlerAlias == null) {
            return null;
        }
        Class<?> type = resolveClass(typeHandlerAlias);
        if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
            throw new OrmException("Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
        }
        @SuppressWarnings("unchecked") // already verified it is a TypeHandler
        Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
        return resolveTypeHandler(javaType, typeHandlerType);
    }

    public TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        // javaType ignored for injected handlers see issue #746 for full detail
        TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
        if (handler == null) {
            // 没有则创建一个
            handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
        }
        return handler;
    }

    public <T> Class<? extends T> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }

    /**
     * 获取对象属性的javaType
     *
     * @param resultType 对象
     * @param property   属性名称
     * @param javaType   原javaType
     * @return 对象属性的javaType
     */
    public Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
                javaType = metaResultType.getSetterType(property);
            } catch (Exception e) {
                // ignore, 后续会有null检查
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    /**
     * 精确解析sql参数#{}里面的javaType
     * <p>&lt;parameterMap&gt;标签专用（暂未实现）</p>
     *
     * @param resultType 属性所属对象class
     * @param property   属性名称
     * @param javaType   原javaType
     * @param jdbcType   jdbcType数据库类型
     * @return sql参数#{}里面的javaType
     */
    private Class<?> resolveParameterJavaType(Class<?> resultType, String property, Class<?> javaType, JdbcType jdbcType) {
        if (javaType == null) {
            if (JdbcType.CURSOR.equals(jdbcType)) {
                javaType = java.sql.ResultSet.class;
            } else if (Map.class.isAssignableFrom(resultType)) {
                javaType = Object.class;
            } else {
                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
                javaType = metaResultType.getGetterType(property);
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    /**
     * 获取方法的返回值类型
     *
     * @param method 方法
     * @param type   方法所在的类的类型
     * @return 方法的返回值类型class
     */
    public Class<?> getReturnType(Method method, Class<?> type) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // actual type 同样有可能是 parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // 支持 List<byte[]>，注意：仅仅只是byte[]、Byte[]数组，本质上还是取第一列，转成byte数组
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                // Do not look into Maps if there is not MapKey annotation
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    Type returnTypeParameter = actualTypeArguments[1];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            } else if (Optional.class.equals(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type returnTypeParameter = actualTypeArguments[0];
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
        }
        return returnType;
    }
}
