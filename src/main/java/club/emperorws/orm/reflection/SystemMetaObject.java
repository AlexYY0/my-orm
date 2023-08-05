package club.emperorws.orm.reflection;

import club.emperorws.orm.reflection.factory.DefaultObjectFactory;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import club.emperorws.orm.reflection.wrapper.ObjectWrapperFactory;

/**
 * 系统默认的一些元对象
 *
 * @author: EmperorWS
 * @date: 2023/4/26 16:16
 * @description: SystemMetaObject: 系统默认的一些元对象
 */
public final class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(new NullObject(), DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    private static class NullObject {
    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
    }
}
