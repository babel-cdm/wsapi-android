package library.webserviceapi.exception;


public class EmptyTypeRequestException extends BaseException{
    public EmptyTypeRequestException() {
        super("No se ha indicado el tipo de petición");
    }
}
