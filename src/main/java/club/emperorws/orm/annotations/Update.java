package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * orm的修改sql标志注解
 *
 * @author: EmperorWS
 * @date: 2023/4/17 12:08
 * @description: Update: orm的修改sql标志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Update {

    /**
     * 代表查询结果的类型 resultType
     *
     * @return 查询结果的类型
     */
    String resultType();
}
