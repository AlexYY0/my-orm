package club.emperorws.orm.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * 真正的执行反射
 *
 * @author: EmperorWS
 * @date: 2023/4/24 14:20
 * @description: Invoker: 真正的执行反射
 */
public interface Invoker {
    Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

    Class<?> getType();
}
