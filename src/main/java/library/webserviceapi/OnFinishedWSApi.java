package library.webserviceapi;

import com.squareup.okhttp.Headers;

public interface OnFinishedWSApi {

    void onError(String id, String error);
    void onSuccess(String id,  Headers header, String data);
    void onException(String id, String exception);
    void onTimeout(String id);
}
