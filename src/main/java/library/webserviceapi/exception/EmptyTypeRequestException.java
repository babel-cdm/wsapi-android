package library.webserviceapi.exception;

/**
 * Created by BABEL SI.
 */
public class EmptyTypeRequestException extends BaseException{
    public EmptyTypeRequestException() {
        super("No se ha indicado el tipo de petición");
    }
}
