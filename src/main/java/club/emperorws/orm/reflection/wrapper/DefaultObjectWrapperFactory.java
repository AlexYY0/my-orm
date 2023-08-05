package club.emperorws.orm.reflection.wrapper;

import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.reflection.MetaObject;

/**
 * 默认的对象包装器的创建工厂
 *
 * @author: EmperorWS
 * @date: 2023/4/24 18:07
 * @description: DefaultObjectWrapperFactory: 默认的对象包装器的创建工厂
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new OrmException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
