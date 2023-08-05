package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * orm的查询sql标志注解
 *
 * @author: EmperorWS
 * @date: 2023/4/17 12:02
 * @description: Select: orm的查询sql标志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {

    /**
     * 代表查询结果的类型 resultType
     *
     * @return 查询结果的类型
     */
    String resultType();
}
