package club.emperorws.orm.logging.jdbc;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Statement的代理类，专注于Debug模式下的日志打印
 *
 * @author: EmperorWS
 * @date: 2023/7/28 17:24
 * @description: StatementLogger: Statement的代理类，专注于Debug模式下的日志打印
 */
public class StatementLogger extends BaseJdbcLogger implements InvocationHandler {

    private final Statement statement;

    private StatementLogger(Statement stmt, Log statementLog) {
        super(statementLog);
        this.statement = stmt;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }
            if (EXECUTE_METHODS.contains(method.getName())) {
                if (isDebugEnabled()) {
                    debug(" Executing: " + removeExtraWhitespace((String) params[0]), true);
                }
                if ("executeQuery".equals(method.getName())) {
                    ResultSet rs = (ResultSet) method.invoke(statement, params);
                    return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog);
                } else {
                    return method.invoke(statement, params);
                }
            } else if ("getResultSet".equals(method.getName())) {
                ResultSet rs = (ResultSet) method.invoke(statement, params);
                return rs == null ? null : ResultSetLogger.newInstance(rs, statementLog);
            } else {
                return method.invoke(statement, params);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    public static Statement newInstance(Statement stmt, Log statementLog) {
        InvocationHandler handler = new StatementLogger(stmt, statementLog);
        ClassLoader cl = Statement.class.getClassLoader();
        return (Statement) Proxy.newProxyInstance(cl, new Class[]{Statement.class}, handler);
    }

    /**
     * return the wrapped statement.
     *
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }
}
