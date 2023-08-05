package club.emperorws.orm.exception;

/**
 * 异常处理工厂，主要是封装{@link ErrorContext}异常
 *
 * @author: EmperorWS
 * @date: 2023/4/28 11:53
 * @description: ExceptionFactory: 异常处理工厂，主要
 */
public class ExceptionFactory {

    private ExceptionFactory() {
        // Prevent Instantiation
    }

    public static RuntimeException wrapException(String message, Exception e) {
        return new OrmException(ErrorContext.instance().message(message).cause(e).toString(), e);
    }
}
