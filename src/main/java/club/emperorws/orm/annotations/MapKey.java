package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * Mapper返回结果为 {@link java.util.Map}，key为属性名称（MapKey.value），value为返回结果对象
 *
 * <p>
 * <b>怎么使用:</b>
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;MapKey("id")
 *   &#064;Select("SELECT id, name FROM users WHERE name LIKE #{name} || '%")
 *   Map&lt;Integer, User&gt; selectByStartingWithName(String name);
 * }
 * </pre>
 *
 * @author: EmperorWS
 * @date: 2023/4/27 16:59
 * @description: MapKey:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapKey {
    /**
     * Returns the property name(or column name) for a key value of {@link java.util.Map}.
     *
     * @return the property name(or column name)
     */
    String value();
}
