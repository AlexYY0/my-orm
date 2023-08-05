package club.emperorws.orm.bingding;

import club.emperorws.orm.bingding.MapperProxy.MapperMethodInvoker;
import club.emperorws.orm.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mapper动态代理类的创建工厂（每一个Mapper，都有自己单独的MapperProxyFactory）
 *
 * @author: EmperorWS
 * @date: 2023/5/15 16:15
 * @description: MapperProxyFactory: Mapper动态代理类的创建工厂（每一个Mapper，都有自己单独的MapperProxyFactory）
 */
public class MapperProxyFactory<T> {

    /**
     * Mapper的class类型
     */
    private final Class<T> mapperInterface;

    /**
     * Mapper里的所有代理方法的缓存
     */
    private final Map<Method, MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MapperMethodInvoker> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession) {
        //每次都需要新的sqlSession，无法缓存，只能缓存代理
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }
}
