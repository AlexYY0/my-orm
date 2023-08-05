package club.emperorws.orm.reflection;

import club.emperorws.orm.util.MapUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 默认的反射工厂
 *
 * @author: EmperorWS
 * @date: 2023/4/24 18:10
 * @description: DefaultReflectorFactory: 默认的反射工厂
 */
public class DefaultReflectorFactory implements ReflectorFactory {

    private boolean classCacheEnabled = true;

    /**
     * class-Reflector之间的映射集合缓存
     */
    private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();

    public DefaultReflectorFactory() {
    }

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnabled;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
        this.classCacheEnabled = classCacheEnabled;
    }

    @Override
    public Reflector findForClass(Class<?> type) {
        if (classCacheEnabled) {
            return MapUtil.computeIfAbsent(reflectorMap, type, Reflector::new);
        } else {
            return new Reflector(type);
        }
    }
}
