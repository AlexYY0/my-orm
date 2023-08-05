package club.emperorws.orm.logging.jdbc;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.util.ArrayUtil;

import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JDBC日志代理类的基类
 *
 * @author: EmperorWS
 * @date: 2023/7/28 16:12
 * @description: BaseJdbcLogger: JDBC日志代理类的基类
 */
public class BaseJdbcLogger {

    /**
     * PreparedStatement的set方法集合，方便获取sql预编译时的？对应的参数列数据
     */
    protected static final Set<String> SET_METHODS;

    /**
     * 满足条件的SQL执行方法，才会打印日志
     */
    protected static final Set<String> EXECUTE_METHODS = new HashSet<>();

    /**
     * 预编译的参数列数据信息
     */
    private final Map<Object, Object> columnMap = new HashMap<>();
    private final List<Object> columnNames = new ArrayList<>();
    private final List<Object> columnValues = new ArrayList<>();

    protected final Log statementLog;

    /**
     * Default constructor
     *
     * @param log 日志打印接口
     */
    public BaseJdbcLogger(Log log) {
        this.statementLog = log;
    }

    static {
        SET_METHODS = Arrays.stream(PreparedStatement.class.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("set"))
                .filter(method -> method.getParameterCount() > 1)
                .map(Method::getName)
                .collect(Collectors.toSet());

        EXECUTE_METHODS.add("execute");
        EXECUTE_METHODS.add("executeUpdate");
        EXECUTE_METHODS.add("executeQuery");
        EXECUTE_METHODS.add("addBatch");
    }

    protected void setColumn(Object key, Object value) {
        columnMap.put(key, value);
        columnNames.add(key);
        columnValues.add(value);
    }

    protected Object getColumn(Object key) {
        return columnMap.get(key);
    }

    /**
     * 参数列数据
     *
     * @return 参数列数据
     */
    protected String getParameterValueString() {
        List<Object> typeList = new ArrayList<>(columnValues.size());
        for (Object value : columnValues) {
            if (value == null) {
                typeList.add("null");
            } else {
                typeList.add(objectValueString(value) + "(" + value.getClass().getSimpleName() + ")");
            }
        }
        final String parameters = typeList.toString();
        return parameters.substring(1, parameters.length() - 1);
    }

    protected String objectValueString(Object value) {
        if (value instanceof Array) {
            try {
                return ArrayUtil.toString(((Array) value).getArray());
            } catch (SQLException e) {
                return value.toString();
            }
        }
        return value.toString();
    }

    protected String getColumnString() {
        return columnNames.toString();
    }

    protected void clearColumnInfo() {
        columnMap.clear();
        columnNames.clear();
        columnValues.clear();
    }

    protected String removeExtraWhitespace(String original) {
        return original.replaceAll(" \t\n\r\f", " ");
    }

    protected boolean isDebugEnabled() {
        return statementLog.isDebugEnabled();
    }

    protected boolean isTraceEnabled() {
        return statementLog.isTraceEnabled();
    }

    protected void debug(String text, boolean input) {
        if (statementLog.isDebugEnabled()) {
            statementLog.debug(prefix(input) + text);
        }
    }

    protected void trace(String text, boolean input) {
        if (statementLog.isTraceEnabled()) {
            statementLog.trace(prefix(input) + text);
        }
    }

    private String prefix(boolean isInput) {
        if (isInput) {
            return "==> ";
        } else {
            return "<== ";
        }
    }
}
