package club.emperorws.orm.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Map工具类
 *
 * @author: EmperorWS
 * @date: 2023/4/24 17:20
 * @description: MapUtil: Map工具类
 */
public class MapUtil {

    private MapUtil() {
        super();
    }

    /**
     * <p>Copy by other</p>
     * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
     * This class should be removed once we drop Java 8 support.
     *
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Map.entry(key, value) alternative for Java 8.
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
