package club.emperorws.orm.reflection.invoker;

import club.emperorws.orm.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * 如果没有set方法，则直接操作Field
 *
 * @author: EmperorWS
 * @date: 2023/4/24 15:29
 * @description: SetFieldInvoker: 如果没有set方法，则直接操作Field
 */
public class SetFieldInvoker implements Invoker {
    private final Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException {
        try {
            field.set(target, args[0]);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                field.set(target, args[0]);
            } else {
                throw e;
            }
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
