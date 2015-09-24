package library.webserviceapi;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import library.utils.async.AsyncJob;
import library.utils.network.NetworkManager;

@SuppressWarnings("unused")
public class WSApi {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final long DEFAULT_SECONDS_TIMEOUT = 60;

    public enum Type {GET, POST, PUT, DELETE}

    public static List<Integer> httpCodeOK = new ArrayList<>();

    static {
        httpCodeOK.add(200);
        httpCodeOK.add(201);
        httpCodeOK.add(202);
        httpCodeOK.add(213);
    }

    private OkHttpClient mClient;
    private RequestParams params;

    public WSApi setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public WSApi() {
        this.mClient = new OkHttpClient();
    }

    class Result {

        String id;
        String data;
        Headers header;
        String exception;
        int code;
        boolean timeout;
    }

    public void execute() {
        if (params.url == null)
            params.listener.onError(params.id, "No se ha indicado URL");
        else if (params.type == null)
            params.listener.onError(params.id, "No se ha indicado el tipo de petición");
        else {
            new AsyncJob.AsyncJobBuilder<Result>()
                    .doInBackground(new AsyncJob.AsyncAction<Result>() {
                        @Override
                        public Result doAsync() {

                            Result result = new Result();
                            result.id = params.id;

                            if (params.getSecondsTimeout() != 0) {
                                mClient.setConnectTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
                                mClient.setReadTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
                                mClient.setWriteTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
                            } else {
                                mClient.setConnectTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
                                mClient.setReadTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
                                mClient.setWriteTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
                            }

                            Request request = doRequest();

                            try {
                                Response response = mClient.newCall(request).execute();
                                result.data = response.body().string();
                                result.header = response.headers();
                                result.code = response.code();
                            } catch (SocketTimeoutException socketTimeout) {
                                result.timeout = true;
                            } catch (final IOException e) {
                                result.exception = e.toString();
                            }
                            return result;
                        }
                    })
                    .doWhenFinished(new AsyncJob.AsyncResultAction<Result>() {
                        @Override
                        public void onResult(Result result) {
                            Log.d("API RESULT", "Code " + result.code);
                            if (result.timeout) {
                                params.listener.onTimeout(result.id);
                            } else if (result.exception != null) {
                                params.listener.onException(result.id, result.exception);
                            } else if (!httpCodeOK.contains(result.code)) {
                                params.listener.onError(result.id, result.data);
                            } else {
                                params.listener.onSuccess(result.id, result.header, result.data.replaceAll("\\p{C}", ""));
                            }
                        }

                    }).create().start();

        }
    }

    public void setPinningCertificate(String hostname, String publicKey) {
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(hostname, publicKey) //"sha1/BOGUSPIN")
                .build();

        mClient.setCertificatePinner(certificatePinner);
    }

    private Request doRequest() {
        Request.Builder request = new Request.Builder();
        if (params.urlParams == null) {
            request.url(params.url);
        } else {
            String url = params.url;
            Iterator it = params.urlParams.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                url += "?" + e.getKey() + "=" + e.getValue();
            }

            request.url(url);
        }
        if (!params.type.equals(params.type.GET)) {
            RequestBody body = RequestBody.create(JSON, params.body);
            switch (params.type) {
                case POST:
                    request.post(body);
                    break;
                case PUT:
                    request.put(body);
                    break;
                case DELETE:
                    request.delete(body);
                    break;
            }
        }

        if (params.header != null) {
            for (Map.Entry<String, String> entry : params.header.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        return request.build();

    }


}