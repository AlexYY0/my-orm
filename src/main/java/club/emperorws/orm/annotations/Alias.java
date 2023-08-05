package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * 标注类型别名的注解
 * <p>
 * <b>How to use:</b>
 * <pre>
 *  &#064;Alias("Email")
 *  public class UserEmail {
 *    // ...
 *  }
 *  </pre>
 *
 * @author: EmperorWS
 * @date: 2023/5/8 10:09
 * @description: Alias: 标注类型别名的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Alias {
    /**
     * Return the alias name.
     *
     * @return the alias name
     */
    String value();
}
