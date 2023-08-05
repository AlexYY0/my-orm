package club.emperorws.orm.reflection.invoker;

import club.emperorws.orm.exception.OrmException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射执行方法有歧义时，则直接抛出异常
 *
 * @author: EmperorWS
 * @date: 2023/4/24 15:34
 * @description: AmbiguousMethodInvoker: 反射执行方法有歧义时，则直接抛出异常
 */
public class AmbiguousMethodInvoker extends MethodInvoker {
    private final String exceptionMessage;

    public AmbiguousMethodInvoker(Method method, String exceptionMessage) {
        super(method);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        throw new OrmException(exceptionMessage);
    }
}
