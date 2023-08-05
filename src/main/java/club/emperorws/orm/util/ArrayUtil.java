package club.emperorws.orm.util;

import java.util.Arrays;

/**
 * Array工具（本可以使用第三方工具，但是我不想与第三方工具耦合）
 *
 * @author: EmperorWS
 * @date: 2023/7/28 17:05
 * @description: ArrayUtil: Array工具
 */
public class ArrayUtil {

    private ArrayUtil() {
    }

    /**
     * 判断数据是否为空
     *
     * @param array 长度
     * @return 数组对象为null或者长度为 0 时，返回 false
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组
     * @return 数组对象内含有任意对象时返回 true
     * @see ArrayUtils#isEmpty(Object[])
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (ObjectUtil.isNull(element)) {
                    return true;
                }
            }
        }
        return array == null;
    }

    public static int hashCode(Object obj) {
        if (obj == null) {
            // for consistency with Arrays#hashCode() and Objects#hashCode()
            return 0;
        }
        final Class<?> clazz = obj.getClass();
        if (!clazz.isArray()) {
            return obj.hashCode();
        }
        final Class<?> componentType = clazz.getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.hashCode((long[]) obj);
        } else if (int.class.equals(componentType)) {
            return Arrays.hashCode((int[]) obj);
        } else if (short.class.equals(componentType)) {
            return Arrays.hashCode((short[]) obj);
        } else if (char.class.equals(componentType)) {
            return Arrays.hashCode((char[]) obj);
        } else if (byte.class.equals(componentType)) {
            return Arrays.hashCode((byte[]) obj);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.hashCode((boolean[]) obj);
        } else if (float.class.equals(componentType)) {
            return Arrays.hashCode((float[]) obj);
        } else if (double.class.equals(componentType)) {
            return Arrays.hashCode((double[]) obj);
        } else {
            return Arrays.hashCode((Object[]) obj);
        }
    }

    public static boolean equals(Object thisObj, Object thatObj) {
        if (thisObj == null) {
            return thatObj == null;
        } else if (thatObj == null) {
            return false;
        }
        final Class<?> clazz = thisObj.getClass();
        if (!clazz.equals(thatObj.getClass())) {
            return false;
        }
        if (!clazz.isArray()) {
            return thisObj.equals(thatObj);
        }
        final Class<?> componentType = clazz.getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.equals((long[]) thisObj, (long[]) thatObj);
        } else if (int.class.equals(componentType)) {
            return Arrays.equals((int[]) thisObj, (int[]) thatObj);
        } else if (short.class.equals(componentType)) {
            return Arrays.equals((short[]) thisObj, (short[]) thatObj);
        } else if (char.class.equals(componentType)) {
            return Arrays.equals((char[]) thisObj, (char[]) thatObj);
        } else if (byte.class.equals(componentType)) {
            return Arrays.equals((byte[]) thisObj, (byte[]) thatObj);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.equals((boolean[]) thisObj, (boolean[]) thatObj);
        } else if (float.class.equals(componentType)) {
            return Arrays.equals((float[]) thisObj, (float[]) thatObj);
        } else if (double.class.equals(componentType)) {
            return Arrays.equals((double[]) thisObj, (double[]) thatObj);
        } else {
            return Arrays.equals((Object[]) thisObj, (Object[]) thatObj);
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        final Class<?> clazz = obj.getClass();
        if (!clazz.isArray()) {
            return obj.toString();
        }
        final Class<?> componentType = obj.getClass().getComponentType();
        if (long.class.equals(componentType)) {
            return Arrays.toString((long[]) obj);
        } else if (int.class.equals(componentType)) {
            return Arrays.toString((int[]) obj);
        } else if (short.class.equals(componentType)) {
            return Arrays.toString((short[]) obj);
        } else if (char.class.equals(componentType)) {
            return Arrays.toString((char[]) obj);
        } else if (byte.class.equals(componentType)) {
            return Arrays.toString((byte[]) obj);
        } else if (boolean.class.equals(componentType)) {
            return Arrays.toString((boolean[]) obj);
        } else if (float.class.equals(componentType)) {
            return Arrays.toString((float[]) obj);
        } else if (double.class.equals(componentType)) {
            return Arrays.toString((double[]) obj);
        } else {
            return Arrays.toString((Object[]) obj);
        }
    }
}
