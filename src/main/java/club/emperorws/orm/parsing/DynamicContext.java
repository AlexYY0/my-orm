package club.emperorws.orm.parsing;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.reflection.MetaObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态SQL解析过程中，请求参数的上下文存储传递
 *
 * @author: EmperorWS
 * @date: 2023/5/6 17:27
 * @description: DynamicContext: 动态SQL解析过程中，请求参数的上下文存储传递
 */
public class DynamicContext {

    /**
     * 再一次封装请求参数
     */
    public static final String PARAMETER_OBJECT_KEY = "_parameter";

    /**
     * 多数据库切换，ignore，暂时用不到
     */
    public static final String DATABASE_ID_KEY = "_databaseId";

    private Configuration configuration;

    private final ContextMap bindings;

    private int uniqueNumber = 0;

    public DynamicContext(Configuration configuration, Object parameterObject) {
        this.configuration = configuration;
        if (parameterObject != null && !(parameterObject instanceof Map)) {
            //parameterObject = ParamMap.class不会进入，但是parameterObject有可能等于实体参数对象（查看代码ParamNameResolver.getNamedParams）
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            //是否是TypeHandlerRegistry已注册的JVM已知类型
            boolean existsTypeHandler = configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass());
            bindings = new ContextMap(metaObject, existsTypeHandler);
        } else {
            bindings = new ContextMap(null, false);
        }
        bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
        bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    public void bind(String name, Object value) {
        bindings.put(name, value);
    }

    public int getUniqueNumber() {
        return uniqueNumber++;
    }

    /**
     * 主要存储运行过程中产生的临时变量
     */
    static class ContextMap extends HashMap<String, Object> {

        private static final long serialVersionUID = 2977601501966151582L;

        private final MetaObject parameterMetaObject;

        /**
         * 是否是TypeHandlerRegistry已注册的JVM已知类型
         */
        private final boolean fallbackParameterObject;

        public ContextMap(MetaObject parameterMetaObject, boolean fallbackParameterObject) {
            this.parameterMetaObject = parameterMetaObject;
            this.fallbackParameterObject = fallbackParameterObject;
        }

        @Override
        public Object get(Object key) {
            String strKey = (String) key;
            if (super.containsKey(strKey)) {
                return super.get(strKey);
            }

            //目测以下代码永远也不会执行
            if (parameterMetaObject == null) {
                return null;
            }

            //是TypeHandlerRegistry已注册的JVM已知类型，直接返回原对象参数
            if (fallbackParameterObject && !parameterMetaObject.hasGetter(strKey)) {
                return parameterMetaObject.getOriginalObject();
            } else {
                //从对象中获取参数
                return parameterMetaObject.getValue(strKey);
            }
        }
    }
}
