package library.webserviceapi;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import library.utils.async.AsyncJob;

@SuppressWarnings("unused")
public class WSApi {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private enum Type {GET, POST, PUT, DELETE}

    class Result {
        String id;
        String data;
        Headers header;
        String exception;
    }

    OkHttpClient mClient = new OkHttpClient();

    public void setPinningCertificate(String hostname, String publicKey) {
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(hostname, publicKey) //"sha1/BOGUSPIN")
                .build();

        mClient.setCertificatePinner(certificatePinner);
    }

    public void get(String url, OnFinishedWSApi listener) {
        get(null, url, null, listener);
    }

    public void get(String id, String url, Map<String, String> header, OnFinishedWSApi listener) {
        execute(Type.GET, id, url, header, null, listener);
    }

    public void post(String id, String url, Map<String, String> header, String json, OnFinishedWSApi listener) {
        execute(Type.POST, id, url, header, json, listener);
    }

    private void execute(final Type type, final String id, final String url, final Map<String, String> header, final String json, final OnFinishedWSApi listener) {
        new AsyncJob.AsyncJobBuilder<Result>()
                .doInBackground(new AsyncJob.AsyncAction<Result>() {
                    @Override
                    public Result doAsync() {

                        Result result = new Result();
                        result.id = id;

                        Request request = null;
                        switch (type) {
                            case GET:
                                request = doGet(url, header);
                                break;
                            case POST:
                                request = doPost(url, header, json);
                                break;
                            case PUT:
                                break;
                            case DELETE:
                                break;
                        }

                        try {
                            Response response = mClient.newCall(request).execute();
                            result.data = response.body().string();
                            result.header = response.headers();

                        } catch (final IOException e) {
                            result.exception = e.toString();
                        }
                        return result;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Result>() {
                    @Override
                    public void onResult(Result result) {
                        if (result.exception != null) {
                            listener.onError(result.id, result.exception);
                        } else {
                            listener.onSuccess(result.id, result.header, result.data.replaceAll("\\p{C}", ""));
                        }
                    }

                }).create().start();
    }

    private Request doGet(String url, Map<String, String> header) {

        Request.Builder request = new Request.Builder();
        request.url(url);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }

        return request.build();
    }

    private Request doPost(String url, Map<String, String> header, String json) {

        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder request = new Request.Builder();
        request.url(url);
        request.post(body);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        return request.build();
    }
}