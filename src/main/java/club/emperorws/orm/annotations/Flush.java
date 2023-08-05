package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * 供Batch模式使用，真正的执行
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;Flush
 *   List&lt;BatchResult&gt; flush();
 * }
 * </pre>
 *
 * @author: EmperorWS
 * @date: 2023/5/10 15:22
 * @description: Flush: 供Batch模式使用，真正的执行
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flush {
}
