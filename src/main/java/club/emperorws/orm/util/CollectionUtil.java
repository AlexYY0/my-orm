package club.emperorws.orm.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * 集合工具类
 *
 * @author: EmperorWS
 * @date: 2023/5/4 17:34
 * @description: CollectionUtil: 集合工具类
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }
}
