package club.emperorws.orm.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 对象工具类
 *
 * @author: EmperorWS
 * @date: 2023/8/5 14:56
 * @description: ObjectUtil: 对象工具类
 */
public class ObjectUtil {

    private ObjectUtil() {
    }

    /**
     * 检查对象是否为null<br>
     * 判断标准为：
     *
     * <pre>
     * 1. == null
     * 2. equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNull(Object obj) {
        //noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }

    /**
     * 检查对象是否不为null
     * <pre>
     * 1. != null
     * 2. not equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为非null
     */
    public static boolean isNotNull(Object obj) {
        //noinspection ConstantConditions
        return null != obj && false == obj.equals(null);
    }

    /**
     * 比较两个对象是否不相等。<br>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否不等
     * @since 3.0.7
     */
    public static boolean notEqual(Object obj1, Object obj2) {
        return false == equal(obj1, obj2);
    }

    /**
     * 比较两个对象是否相等，此方法是 {@link #equal(Object, Object)}的别名方法。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see #equal(Object, Object)
     * @since 5.4.3
     */
    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see Objects#equals(Object, Object)
     */
    public static boolean equal(Object obj1, Object obj2) {
        if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
            return equals((BigDecimal) obj1, (BigDecimal) obj2);
        }
        return Objects.equals(obj1, obj2);
    }


    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        //noinspection NumberEquality
        if (bigNum1 == bigNum2) {
            // 如果用户传入同一对象，省略compareTo以提高性能。
            return true;
        }
        if (bigNum1 == null || bigNum2 == null) {
            return false;
        }
        return 0 == bigNum1.compareTo(bigNum2);
    }
}
