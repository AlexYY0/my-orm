package club.emperorws.orm.reflection;

import club.emperorws.orm.reflection.invoker.GetFieldInvoker;
import club.emperorws.orm.reflection.invoker.Invoker;
import club.emperorws.orm.reflection.invoker.MethodInvoker;
import club.emperorws.orm.reflection.property.PropertyTokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 元对象class相关处理
 *
 * @author: EmperorWS
 * @date: 2023/4/26 17:15
 * @description: MetaClass: 元对象class相关处理
 */
public class MetaClass {

    private final ReflectorFactory reflectorFactory;
    private final Reflector reflector;

    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
        return new MetaClass(type, reflectorFactory);
    }

    /**
     * 获取真正的属性名称
     *
     * @param name                原属性名称
     * @param useCamelCaseMapping 是否使用驼峰映射
     * @return 真正的属性名称
     */
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            name = name.replace("_", "");
        }
        return findProperty(name);
    }

    /**
     * 获取真正的属性名称
     *
     * @param name 原属性名称
     * @return 真正的属性名称
     */
    public String findProperty(String name) {
        StringBuilder prop = buildProperty(name, new StringBuilder());
        return prop.length() > 0 ? prop.toString() : null;
    }

    public String[] getGetterNames() {
        return reflector.getGetablePropertyNames();
    }

    public String[] getSetterNames() {
        return reflector.getSetablePropertyNames();
    }

    /**
     * 获取set属性的class类型
     *
     * @param name 属性名称
     * @return set属性的class类型
     */
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            //获取父属性的MetaClass（通过getGetterType获取，不支持orgList[0].orgName）
            //todo 为什么不支持orgList[0].orgName这种方式？set不可能出现这种情况，get则会？
            MetaClass metaProp = metaClassForProperty(prop.getName());
            //再获取子属性的class类型
            return metaProp.getSetterType(prop.getChildren());
        } else {
            return reflector.getSetterType(prop.getName());
        }
    }

    /**
     * 获取get属性的class类型
     *
     * @param name 属性名称
     * @return get属性的class类型
     */
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            //获取父属性的MetaClass（通过getGetterType获取，支持orgList[0].orgName）
            MetaClass metaProp = metaClassForProperty(prop);
            //再获取子属性的class类型
            return metaProp.getGetterType(prop.getChildren());
        }
        // Resolve the type inside a Collection Object
        return getGetterType(prop);
    }

    /**
     * 获取属性的MetaClass
     *
     * @param name 属性名称
     * @return 属性名称对应的MetaClass
     */
    public MetaClass metaClassForProperty(String name) {
        Class<?> propType = reflector.getGetterType(name);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    /**
     * 获取属性的MetaClass
     *
     * @param prop 属性迭代器
     * @return 属性名称对应的MetaClass
     */
    private MetaClass metaClassForProperty(PropertyTokenizer prop) {
        Class<?> propType = getGetterType(prop);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    /**
     * 获取get属性的class类型（支持处理集合数组的情况：orgList[0].orgName）
     *
     * @param prop 属性迭代器
     * @return 处理-获取集合的返回值类型（包含泛型）
     */
    private Class<?> getGetterType(PropertyTokenizer prop) {
        Class<?> type = reflector.getGetterType(prop.getName());
        //支持处理集合数组的情况：orgList[0].orgName
        if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
            //获取get的返回值类型（找到原方法的返回值或Field的属性）
            Type returnType = getGenericGetterType(prop.getName());
            if (returnType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    returnType = actualTypeArguments[0];
                    if (returnType instanceof Class) {
                        type = (Class<?>) returnType;
                    } else if (returnType instanceof ParameterizedType) {
                        //ParameterizedType里面如果还有更深层次的泛型，则无法获取例如List<Org<String>>，String无法获取，只能拿到Object.class
                        type = (Class<?>) ((ParameterizedType) returnType).getRawType();
                    }
                }
            }
        }
        return type;
    }

    /**
     * 获取get的返回值类型（找到原方法的返回值或Field的属性）
     *
     * @param propertyName 属性名称
     * @return 获取get的返回值类型
     */
    private Type getGenericGetterType(String propertyName) {
        try {
            Invoker invoker = reflector.getGetInvoker(propertyName);
            //获取get对应的方法，找出其返回值类型
            if (invoker instanceof MethodInvoker) {
                Field declaredMethod = MethodInvoker.class.getDeclaredField("method");
                declaredMethod.setAccessible(true);
                Method method = (Method) declaredMethod.get(invoker);
                return TypeParameterResolver.resolveReturnType(method, reflector.getType());
            } else if (invoker instanceof GetFieldInvoker) {
                //通过属性Field，找出其值类型
                Field declaredField = GetFieldInvoker.class.getDeclaredField("field");
                declaredField.setAccessible(true);
                Field field = (Field) declaredField.get(invoker);
                return TypeParameterResolver.resolveFieldType(field, reflector.getType());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Ignored
        }
        return null;
    }

    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (reflector.hasSetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop.getName());
                return metaProp.hasSetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasSetter(prop.getName());
        }
    }

    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (reflector.hasGetter(prop.getName())) {
                MetaClass metaProp = metaClassForProperty(prop);
                return metaProp.hasGetter(prop.getChildren());
            } else {
                return false;
            }
        } else {
            return reflector.hasGetter(prop.getName());
        }
    }

    public Invoker getGetInvoker(String name) {
        return reflector.getGetInvoker(name);
    }

    public Invoker getSetInvoker(String name) {
        return reflector.getSetInvoker(name);
    }

    /**
     * 属性名称的构造器
     *
     * @param name    属性名称
     * @param builder 最终属性名称StringBuilder
     * @return 最终属性名称StringBuilder
     */
    private StringBuilder buildProperty(String name, StringBuilder builder) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            String propertyName = reflector.findPropertyName(prop.getName());
            if (propertyName != null) {
                builder.append(propertyName);
                builder.append(".");
                MetaClass metaProp = metaClassForProperty(propertyName);
                metaProp.buildProperty(prop.getChildren(), builder);
            }
        } else {
            String propertyName = reflector.findPropertyName(name);
            if (propertyName != null) {
                builder.append(propertyName);
            }
        }
        return builder;
    }

    public boolean hasDefaultConstructor() {
        return reflector.hasDefaultConstructor();
    }
}
