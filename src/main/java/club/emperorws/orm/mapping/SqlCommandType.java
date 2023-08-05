package club.emperorws.orm.mapping;

/**
 * sql语句的执行类型
 *
 * @author: EmperorWS
 * @date: 2023/5/6 15:00
 * @description: SqlCommandType: sql语句的执行类型
 */
public enum SqlCommandType {

    /**
     * 未知
     */
    UNKNOWN,

    /**
     * 增
     */
    INSERT,

    /**
     * 改
     */
    UPDATE,

    /**
     * 删
     */
    DELETE,

    /**
     * 查
     */
    SELECT,

    /**
     * Batch最终的执行
     */
    FLUSH
}
