package library.webserviceapi.exception;


public class EmptyURLException extends BaseException {
    public EmptyURLException(){
        super("No se ha indicado la URL para la petición.");
    }
}
