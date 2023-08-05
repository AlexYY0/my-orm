package club.emperorws.orm.reflection.wrapper;

import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.SystemMetaObject;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map对象的包装器
 *
 * @author: EmperorWS
 * @date: 2023/4/26 13:40
 * @description: MapWrapper: Map对象的包装器
 */
public class MapWrapper extends BaseWrapper {

    /**
     * 被包装的map
     */
    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject);
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            //获取prop对应的集合的值
            Object collection = resolveCollection(prop, map);
            //从集合中获取具体的值
            return getCollectionValue(prop, collection);
        } else {
            return map.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            //获取prop对应的集合的值
            Object collection = resolveCollection(prop, map);
            //设置集合中具体的值
            setCollectionValue(prop, collection, value);
        } else {
            map.put(prop.getName(), value);
        }
    }

    /**
     * 获取真正的属性名称
     *
     * @param name                原属性名称
     * @param useCamelCaseMapping 忽略下划线
     * @return 真正的属性名称
     */
    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    /**
     * 允许get的属性名称
     *
     * @return 允许get的属性名称
     */
    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    /**
     * 允许set的属性名称
     *
     * @return 允许set的属性名称
     */
    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    /**
     * 获取set的属性的类型
     *
     * @param name 属性名称
     * @return set的属性的类型
     */
    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    /**
     * 获取get的属性的类型
     *
     * @param name 属性名称
     * @return get的属性的类型
     */
    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    /**
     * 判断是否允许set
     *
     * @param name 属性名称
     * @return 是否允许set
     */
    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    /**
     * 判断是否允许get
     *
     * @param name 属性名称
     * @return 是否允许get
     */
    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (map.containsKey(prop.getIndexedName())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(prop.getName());
        }
    }

    /**
     * 初始化属性的值
     *
     * @param name          属性名称
     * @param prop          属性迭代器
     * @param objectFactory 对象工厂
     * @return 属性的值的元对象
     */
    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(prop, map);
        return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory(), metaObject.getReflectorFactory());
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }

}
