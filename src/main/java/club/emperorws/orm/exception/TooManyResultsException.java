package club.emperorws.orm.exception;

/**
 * TooManyResultsException
 *
 * @author: EmperorWS
 * @date: 2023/5/10 14:42
 * @description: TooManyResultsException: TooManyResultsException
 */
public class TooManyResultsException extends OrmException {

    private static final long serialVersionUID = 8935197089745865786L;

    public TooManyResultsException() {
        super();
    }

    public TooManyResultsException(String message) {
        super(message);
    }

    public TooManyResultsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyResultsException(Throwable cause) {
        super(cause);
    }
}
