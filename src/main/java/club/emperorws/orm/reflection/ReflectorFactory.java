package club.emperorws.orm.reflection;

/**
 * 反射工厂类，主要用于执行反射
 *
 * @author: EmperorWS
 * @date: 2023/4/24 14:12
 * @description: ReflectorFactory: 反射工厂类，主要用于执行反射
 */
public interface ReflectorFactory {

    boolean isClassCacheEnabled();

    void setClassCacheEnabled(boolean classCacheEnabled);

    Reflector findForClass(Class<?> type);
}
