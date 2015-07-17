package library.webserviceapi;

import com.squareup.okhttp.Headers;

public interface OnFinishedWSApi {

    public void onError(String id, String error);
    public void onSuccess(String id,  Headers header, String data);
}
