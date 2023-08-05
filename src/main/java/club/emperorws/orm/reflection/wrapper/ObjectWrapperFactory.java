package club.emperorws.orm.reflection.wrapper;

import club.emperorws.orm.reflection.MetaObject;

/**
 * 对象包装器的创建工厂
 *
 * @author: EmperorWS
 * @date: 2023/4/24 14:11
 * @description: ObjectWrapperFactory: 对象包装器的创建工程
 */
public interface ObjectWrapperFactory {

    boolean hasWrapperFor(Object object);

    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
