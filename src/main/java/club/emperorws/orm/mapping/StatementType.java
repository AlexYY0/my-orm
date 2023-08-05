package club.emperorws.orm.mapping;

/**
 * SQL语句的执行模式
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:28
 * @description: StatementType: SQL语句的执行模式
 */
public enum StatementType {

    /**
     * 不需要预编译的SQL语句执行
     */
    STATEMENT,

    /**
     * 需要预编译的SQL语句执行
     */
    PREPARED,

    /**
     * 存储过程的执行
     */
    CALLABLE
}
