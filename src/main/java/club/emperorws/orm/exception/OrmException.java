package club.emperorws.orm.exception;

/**
 * Type类型处理异常
 *
 * @author: EmperorWS
 * @date: 2023/4/20 11:54
 * @description: TypeException: Type类型处理异常
 */
public class OrmException extends RuntimeException {

    public OrmException() {
        super();
    }

    public OrmException(String message) {
        super(message);
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmException(Throwable cause) {
        super(cause);
    }
}
