package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * 返回值类型说明，当方法里面有ResultHandler参数时，那么方法的返回值就必须为void。
 * 该注解就是告诉orm，sql执行结果，每行数据映射为的对象类型
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;ResultType(User.class)
 *   &#064;Select("SELECT id, name FROM users WHERE name LIKE #{name} || '%' ORDER BY id")
 *   void collectByStartingWithName(String name, ResultHandler&lt;User&gt; handler);
 * }
 * </pre>
 * <p>此时，所有的结果实际上是存储在ResultHandler里面，ResultHandler.getResultList即可获取</p>
 *
 * @author: EmperorWS
 * @date: 2023/5/11 10:10
 * @description: ResultType: 返回值类型说明
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultType {
    /**
     * Returns the return type.
     *
     * @return the return type
     */
    Class<?> value();
}
