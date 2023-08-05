package club.emperorws.orm.result;

import club.emperorws.orm.annotations.MapKey;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.ReflectorFactory;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.reflection.wrapper.ObjectWrapperFactory;

import java.util.Map;

/**
 * {@link MapKey}注解的方法返回结果处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/27 17:02
 * @description: DefaultMapResultHandler: @Mapkey注解的处理器
 */
public class DefaultMapResultHandler<K, V> implements ResultHandler<V> {

    private final Map<K, V> mappedResults;
    private final String mapKey;
    private final ObjectFactory objectFactory;
    private final ObjectWrapperFactory objectWrapperFactory;
    private final ReflectorFactory reflectorFactory;

    @SuppressWarnings("unchecked")
    public DefaultMapResultHandler(String mapKey, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;
        this.reflectorFactory = reflectorFactory;
        this.mappedResults = objectFactory.create(Map.class);
        this.mapKey = mapKey;
    }

    @Override
    public void handleResult(ResultContext<? extends V> context) {
        final V value = context.getResultObject();
        final MetaObject mo = MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
        final K key = (K) mo.getValue(mapKey);
        mappedResults.put(key, value);
    }

    public Map<K, V> getMappedResults() {
        return mappedResults;
    }
}
