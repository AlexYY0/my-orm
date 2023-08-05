package club.emperorws.orm.session;

/**
 * SQL执行类型
 *
 * @author: EmperorWS
 * @date: 2023/5/8 16:59
 * @description: ExecutorType: SQL执行类型
 */
public enum ExecutorType {

    /**
     * 简单SQL执行
     */
    SIMPLE,

    /**
     * 可重复使用的SQL执行
     */
    REUSE,

    /**
     * BATCH批量SQL执行
     */
    BATCH
}
