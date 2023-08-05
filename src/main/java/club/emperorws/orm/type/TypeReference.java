package club.emperorws.orm.type;

import club.emperorws.orm.exception.OrmException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 获取泛型的原始类型
 *
 * @author: EmperorWS
 * @date: 2023/4/20 13:54
 * @description: TypeReference: 获取泛型的原始类型
 */
public class TypeReference<T> {

    //泛型的原始类型
    private final Type rawType;

    protected TypeReference() {
        rawType = getSuperclassTypeParameter(getClass());
    }

    Type getSuperclassTypeParameter(Class<?> clazz) {
        //从一个Class对象中，获取该对象父类接收到的参数化类型
        Type genericSuperclass = clazz.getGenericSuperclass();
        //不是Class类，是泛型类
        if (genericSuperclass instanceof Class) {
            if (TypeReference.class != genericSuperclass) {
                return getSuperclassTypeParameter(clazz.getSuperclass());
            }
            throw new OrmException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
                    + "Remove the extension or add a type parameter to it.");
        }
        //获取泛型<T>中的T类型
        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return rawType;
    }

    public final Type getRawType() {
        return rawType;
    }

    @Override
    public String toString() {
        return rawType.toString();
    }
}
