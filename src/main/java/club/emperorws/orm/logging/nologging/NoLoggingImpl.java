package club.emperorws.orm.logging.nologging;

import club.emperorws.orm.logging.Log;

/**
 * 无日志的Log实现
 *
 * @author: EmperorWS
 * @date: 2023/5/18 16:15
 * @description: NoLoggingImpl: 无日志的Log实现
 */
public class NoLoggingImpl implements Log {

    public NoLoggingImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void error(String s, Throwable e) {
        // Do Nothing
    }

    @Override
    public void error(String s) {
        // Do Nothing
    }

    @Override
    public void debug(String s) {
        // Do Nothing
    }

    @Override
    public void trace(String s) {
        // Do Nothing
    }

    @Override
    public void warn(String s) {
        // Do Nothing
    }
}
