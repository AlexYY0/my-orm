package club.emperorws.orm.reflection.invoker;

import club.emperorws.orm.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * 如果没有get方法，再直接操作field
 *
 * @author: EmperorWS
 * @date: 2023/4/24 15:30
 * @description: GetFieldInvoker: 如果没有get方法，再直接操作field
 */
public class GetFieldInvoker implements Invoker {
    private final Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                return field.get(target);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
