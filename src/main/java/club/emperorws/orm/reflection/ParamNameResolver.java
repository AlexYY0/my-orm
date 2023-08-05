package club.emperorws.orm.reflection;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.annotations.Param;
import club.emperorws.orm.bingding.MapperMethod.ParamMap;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.util.ParamNameUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * DAO、Mapper‘s function请求参数的处理
 * <p>根据观察，参数名称有可能为</p>
 * <p>1. @Param注解直接获取参数名称</p>
 * <p>2. arg0、arg1、arg2</p>
 * <p>3. 0、1、2</p>
 * <p>4. param1, param2、param3</p>
 *
 * @author: EmperorWS
 * @date: 2023/4/27 15:11
 * @description: ParamNameResolver: DAO、Mapper‘s function请求参数的处理
 */
public class ParamNameResolver {

    /**
     * 另一套请求参数名称逻辑：param1、param2、param3
     */
    public static final String GENERIC_NAME_PREFIX = "param";

    /**
     * 是否直接使用方法的参数名称当作DAO（Mapper）方法的参数名
     */
    private final boolean useActualParamName;

    /**
     * 请求参数index-请求参数名称的映射集合
     * <p>注意：map中记录的请求参数index与实际的可能不相符，因为会忽略一些特殊的参数(i.e. {@link ResultHandler},{@link SqlSource},{@link RowBounds})</p>
     * <ul>
     * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
     * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
     * <li>aMethod(int a, ResultHandler rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
     * </ul>
     */
    private final SortedMap<Integer, String> names;

    /**
     * 方法是否有使用{@link Param}注解
     */
    private boolean hasParamAnnotation;

    public ParamNameResolver(Configuration config, Method method) {
        this.useActualParamName = config.isUseActualParamName();
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<>();
        int paramCount = paramAnnotations.length;
        // 从@Param注解中获取参数名称
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            if (isSpecialParameter(paramTypes[paramIndex])) {
                // 跳过特殊的请求参数
                continue;
            }
            String name = null;
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            //通过@Param注解没有拿到参数名称
            if (name == null) {
                // 允许直接使用系统默认的参数名称当作名称（arg0、arg1、arg2）
                if (useActualParamName) {
                    name = getActualParamName(method, paramIndex);
                }
                //还是没有拿到参数名称
                if (name == null) {
                    // 使用index当作名称 ("0", "1", ...)
                    name = String.valueOf(map.size());
                }
            }
            map.put(paramIndex, name);
        }
        names = Collections.unmodifiableSortedMap(map);
    }

    /**
     * 获取封装的mapper请求参数，同时还会添加默认的参数名(param1, param2, ...).
     *
     * @param args 实际请求参数
     * @return 封装的mapper请求参数（key：name，value：参数）
     */
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            return null;
        } else if (!hasParamAnnotation && paramCount == 1) {
            //没有使用@Param注解
            //注意这里，没有使用@Param注解，且参数数量只有一个，则直接返回‘对象’本体
            Object value = args[names.firstKey()];
            //自己封装一下参数（特别处理集合）
            return wrapToMapIfCollection(value, useActualParamName ? names.get(0) : null);
        } else {
            final Map<String, Object> param = new ParamMap<>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                param.put(entry.getValue(), args[entry.getKey()]);
                // 生成默认的参数名称 (param1, param2, ...)
                final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
                // 不要覆盖@Param的名称
                if (!names.containsValue(genericParamName)) {
                    param.put(genericParamName, args[entry.getKey()]);
                }
                i++;
            }
            return param;
        }
    }

    /**
     * 如果参数是集合，需要再包装一下，放到Map里面
     *
     * @param object          参数值
     * @param actualParamName 参数名
     * @return 包装后集合参数
     */
    public static Object wrapToMapIfCollection(Object object, String actualParamName) {
        if (object instanceof Collection) {
            ParamMap<Object> map = new ParamMap<>();
            map.put("collection", object);
            if (object instanceof List) {
                map.put("list", object);
            }
            Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
            return map;
        } else if (object != null && object.getClass().isArray()) {
            ParamMap<Object> map = new ParamMap<>();
            map.put("array", object);
            Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
            return map;
        }
        return object;
    }

    /**
     * 是否是特殊的参数
     *
     * @param clazz 请求参数的类型
     * @return 是否是特殊的参数
     */
    private static boolean isSpecialParameter(Class<?> clazz) {
        return ResultHandler.class.isAssignableFrom(clazz) || RowBounds.class.isAssignableFrom(clazz) || SqlSource.class.isAssignableFrom(clazz);
    }

    /**
     * 获取参数名称集合
     *
     * @return 参数名称集合
     */
    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    /**
     * 获取方法的真实参数名称
     *
     * @param method     方法
     * @param paramIndex 参数index
     * @return 方法的真实参数名称
     */
    private String getActualParamName(Method method, int paramIndex) {
        return ParamNameUtil.getParamNames(method).get(paramIndex);
    }
}
