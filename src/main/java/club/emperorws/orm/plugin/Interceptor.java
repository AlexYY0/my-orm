package club.emperorws.orm.plugin;

import java.util.Properties;

/**
 * todo 等待后续完成
 * 插件拦截器功能，通过反射代理实现
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:51
 * @description: Interceptor: 插件拦截器功能
 */
public interface Interceptor {

    Object intercept(Invocation invocation) throws Throwable;

    default Object plugin(Object target) {
        //return Plugin.wrap(target, this);
        return target;
    }

    default void setProperties(Properties properties) {
        // NOP
    }
}
