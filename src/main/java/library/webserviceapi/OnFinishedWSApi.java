package library.webserviceapi;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.ResponseBody;

public interface OnFinishedWSApi {

    void onError(String id, String error);

    @Deprecated
    void onSuccess(String id, Headers header, String data);

//    void onSuccess(String id, Headers header, ResponseBody data);

    void onException(String id, String exception);

    void onTimeout(String id);
}
