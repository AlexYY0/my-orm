package club.emperorws.orm.result;

/**
 * 自动映射的方式
 *
 * @author: EmperorWS
 * @date: 2023/5/4 17:03
 * @description: AutoMappingBehavior: 自动映射的方式
 */
public enum AutoMappingBehavior {

    /**
     * 禁用自动映射.
     */
    NONE,

    /**
     * 简单映射，不管嵌套结果.
     */
    PARTIAL,

    /**
     * 全量映射，包括嵌套结果.
     */
    FULL
}
