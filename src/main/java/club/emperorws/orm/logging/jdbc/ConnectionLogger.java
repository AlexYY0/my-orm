package club.emperorws.orm.logging.jdbc;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * JDBC Connection的代理，专注于Debug模式下的日志打印
 *
 * @author: EmperorWS
 * @date: 2023/7/28 16:06
 * @description: ConnectionLogger: JDBC Connection的代理，专注于Debug模式下的日志打印
 */
public class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

    private final Connection connection;

    private ConnectionLogger(Connection conn, Log statementLog) {
        super(statementLog);
        this.connection = conn;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params)
            throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }
            if ("prepareStatement".equals(method.getName()) || "prepareCall".equals(method.getName())) {
                if (isDebugEnabled()) {
                    debug(" Preparing: " + removeExtraWhitespace((String) params[0]), true);
                }
                PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
                stmt = PreparedStatementLogger.newInstance(stmt, statementLog);
                return stmt;
            } else if ("createStatement".equals(method.getName())) {
                Statement stmt = (Statement) method.invoke(connection, params);
                stmt = StatementLogger.newInstance(stmt, statementLog);
                return stmt;
            } else {
                return method.invoke(connection, params);
            }
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    /**
     * 创建新的Connection日志打印代理
     *
     * @param conn         Connection
     * @param statementLog 日志打印接口
     * @return Connection日志打印代理
     */
    public static Connection newInstance(Connection conn, Log statementLog) {
        InvocationHandler handler = new ConnectionLogger(conn, statementLog);
        ClassLoader cl = Connection.class.getClassLoader();
        return (Connection) Proxy.newProxyInstance(cl, new Class[]{Connection.class}, handler);
    }

    public Connection getConnection() {
        return connection;
    }
}
