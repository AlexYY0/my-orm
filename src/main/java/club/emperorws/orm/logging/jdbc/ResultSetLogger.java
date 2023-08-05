package club.emperorws.orm.logging.jdbc;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * ResultSet的代理类，专注于Debug模式下的日志打印
 *
 * @author: EmperorWS
 * @date: 2023/7/28 17:28
 * @description: ResultSetLogger: ResultSet的代理类，专注于Debug模式下的日志打印
 */
public class ResultSetLogger extends BaseJdbcLogger implements InvocationHandler {

    /**
     * 需要特殊处理的数据类型BLOB（不显示具体的内容，防止内容太大影响性能）
     */
    private static final Set<Integer> BLOB_TYPES = new HashSet<>();

    /**
     * 是否是第一次触发，第一次触发，需要处理一些数据【打印列名，获取BLOB列序号】
     */
    private boolean first = true;

    /**
     * 数据量统计
     */
    private int rows;

    /**
     * ResultSet真正的结果
     */
    private final ResultSet rs;

    /**
     * BLOB数据类型的列序号（需要特殊标注，打印时不显示具体的内容，防止内容太大影响性能）
     */
    private final Set<Integer> blobColumns = new HashSet<>();

    static {
        BLOB_TYPES.add(Types.BINARY);
        BLOB_TYPES.add(Types.BLOB);
        BLOB_TYPES.add(Types.CLOB);
        BLOB_TYPES.add(Types.LONGNVARCHAR);
        BLOB_TYPES.add(Types.LONGVARBINARY);
        BLOB_TYPES.add(Types.LONGVARCHAR);
        BLOB_TYPES.add(Types.NCLOB);
        BLOB_TYPES.add(Types.VARBINARY);
    }

    private ResultSetLogger(ResultSet rs, Log statementLog) {
        super(statementLog);
        this.rs = rs;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        try {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, params);
            }
            Object o = method.invoke(rs, params);
            if ("next".equals(method.getName())) {
                if ((Boolean) o) {
                    rows++;
                    if (isTraceEnabled()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        final int columnCount = rsmd.getColumnCount();
                        if (first) {
                            first = false;
                            printColumnHeaders(rsmd, columnCount);
                        }
                        printColumnValues(columnCount);
                    }
                } else {
                    debug("     Total: " + rows, false);
                }
            }
            clearColumnInfo();
            return o;
        } catch (Throwable t) {
            throw ExceptionUtil.unwrapThrowable(t);
        }
    }

    private void printColumnHeaders(ResultSetMetaData rsmd, int columnCount) throws SQLException {
        StringJoiner row = new StringJoiner(", ", "   Columns: ", "");
        for (int i = 1; i <= columnCount; i++) {
            if (BLOB_TYPES.contains(rsmd.getColumnType(i))) {
                blobColumns.add(i);
            }
            row.add(rsmd.getColumnLabel(i));
        }
        trace(row.toString(), false);
    }

    private void printColumnValues(int columnCount) {
        StringJoiner row = new StringJoiner(", ", "       Row: ", "");
        for (int i = 1; i <= columnCount; i++) {
            try {
                if (blobColumns.contains(i)) {
                    row.add("<<BLOB>>");
                } else {
                    row.add(rs.getString(i));
                }
            } catch (SQLException e) {
                // generally can't call getString() on a BLOB column
                row.add("<<Cannot Display>>");
            }
        }
        trace(row.toString(), false);
    }

    public static ResultSet newInstance(ResultSet rs, Log statementLog) {
        InvocationHandler handler = new ResultSetLogger(rs, statementLog);
        ClassLoader cl = ResultSet.class.getClassLoader();
        return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
    }

    public ResultSet getRs() {
        return rs;
    }
}
