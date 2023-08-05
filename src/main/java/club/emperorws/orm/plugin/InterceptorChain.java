package club.emperorws.orm.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 插件拦截器的执行链
 *
 * @author: EmperorWS
 * @date: 2023/4/28 15:02
 * @description: InterceptorChain: 插件拦截器的执行链
 */
public class InterceptorChain {

    private final List<Interceptor> interceptors = new ArrayList<>();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

}
