package library.webserviceapi.exception;

/**
 * Created by BABEL SI.
 */
public class EmptyURLException extends BaseException {
    public EmptyURLException(){
        super("No se ha indicado la URL para la petición.");
    }
}
