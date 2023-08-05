package club.emperorws.orm.annotations;

import java.lang.annotation.*;

/**
 * Mapper方法的请求参数名称
 *
 * @author: EmperorWS
 * @date: 2023/4/19 17:26
 * @description: Param: Mapper方法的请求参数名称
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * 参数名称
     *
     * @return 参数名称
     */
    String value();
}
