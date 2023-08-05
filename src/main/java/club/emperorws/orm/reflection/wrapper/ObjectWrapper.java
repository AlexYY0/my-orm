package club.emperorws.orm.reflection.wrapper;

import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * 对象的包装器类
 *
 * @author: EmperorWS
 * @date: 2023/4/24 14:05
 * @description: ObjectWrapper: 对象的包装器类
 */
public interface ObjectWrapper {

    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);

    /**
     * 获取真正的属性名称
     *
     * @param name                原属性名称
     * @param useCamelCaseMapping 是否使用驼峰映射
     * @return 真正的属性名称
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 允许get的属性名称
     *
     * @return 允许get的属性名称
     */
    String[] getGetterNames();

    /**
     * 允许set的属性名称
     *
     * @return 允许set的属性名称
     */
    String[] getSetterNames();

    /**
     * 获取set的属性的类型
     *
     * @param name 属性名称
     * @return set的属性的类型
     */
    Class<?> getSetterType(String name);

    /**
     * 获取get的属性的类型
     *
     * @param name 属性名称
     * @return get的属性的类型
     */
    Class<?> getGetterType(String name);

    /**
     * 判断是否允许set
     *
     * @param name 属性名称
     * @return 是否允许set
     */
    boolean hasSetter(String name);

    /**
     * 判断是否允许get
     *
     * @param name 属性名称
     * @return 是否允许get
     */
    boolean hasGetter(String name);

    /**
     * 初始化属性的值
     *
     * @param name          属性名称
     * @param prop          属性迭代器
     * @param objectFactory 对象工厂
     * @return 属性的值的元对象
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    boolean isCollection();

    void add(Object element);

    <E> void addAll(List<E> element);
}
