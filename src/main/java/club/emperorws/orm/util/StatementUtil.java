package club.emperorws.orm.util;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Statement的相关工具
 *
 * @author: EmperorWS
 * @date: 2023/4/28 16:12
 * @description: StatementUtil: Statement的相关工具
 */
public class StatementUtil {

    private StatementUtil() {
        // NOP
    }

    /**
     * 设置查询的超时时间==事务超时时间
     *
     * @param statement          目标Statement
     * @param queryTimeout       查询超时时间
     * @param transactionTimeout 事务超时时间
     * @throws SQLException 异常
     */
    public static void applyTransactionTimeout(Statement statement, Integer queryTimeout, Integer transactionTimeout) throws SQLException {
        if (transactionTimeout == null) {
            return;
        }
        if (queryTimeout == null || queryTimeout == 0 || transactionTimeout < queryTimeout) {
            statement.setQueryTimeout(transactionTimeout);
        }
    }
}
