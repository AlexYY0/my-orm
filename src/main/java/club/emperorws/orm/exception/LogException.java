package club.emperorws.orm.exception;

/**
 * 日志异常
 *
 * @author: EmperorWS
 * @date: 2023/5/8 10:57
 * @description: LogException: 日志异常
 */
public class LogException extends OrmException {

    private static final long serialVersionUID = 1022924004852350942L;

    public LogException() {
        super();
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }

}
