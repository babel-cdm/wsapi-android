package library.webserviceapi;

public interface OnFinishedWSApi {

    public void onError(String id, String error);
    public void onSuccess(String id, String data);
}
