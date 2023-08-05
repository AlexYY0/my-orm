package club.emperorws.orm.logging;

import java.util.function.Supplier;

/**
 * 通用日志接口
 *
 * @author: EmperorWS
 * @date: 2023/5/8 10:56
 * @description: Log: 通用日志接口
 */
public interface Log {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);

    default void debug(Supplier<String> s) {
        if (isDebugEnabled()) {
            debug(s.get());
        }
    }

    default void trace(Supplier<String> s) {
        if (isTraceEnabled()) {
            trace(s.get());
        }
    }
}
