package club.emperorws.orm.bingding;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.io.ResolverUtil;
import club.emperorws.orm.session.SqlSession;

import java.util.*;

/**
 * Mapper动态代理的注册器
 *
 * @author: EmperorWS
 * @date: 2023/5/15 16:31
 * @description: MapperRegistry: Mapper动态代理的注册器
 */
public class MapperRegistry {

    private final Configuration config;

    /**
     * Mapper动态代理类的创建工厂的缓存，每一个Mapper，都有自己单独的MapperProxyFactory，MapperProxyFactory里面有Method缓存
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new OrmException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new OrmException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    /**
     * 添加Mapper的代理
     *
     * @param packageName 包路径
     */
    public void addMappers(String packageName) {
        addMappers(packageName, Object.class);
    }

    /**
     * 添加Mapper的代理
     *
     * @param packageName 包路径
     * @param superType   Mapper的父类或超类的类型（主要用于筛选出合适的Mapper）
     */
    public void addMappers(String packageName, Class<?> superType) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new OrmException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MapperProxyFactory<>(type));
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }
}
