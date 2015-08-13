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
    public enum Type {GET, POST, PUT, DELETE;}
    private OkHttpClient mClient;
    private Input input;

    class Input {
        String id;
        String type;
        String url;
        String body;
        OnFinishedWSApi listener;
        Map<String, String> header;
    }

    class Result {

        String id;
        String data;
        Headers header;
        String exception;
    }

    public WSApi() {
        this.input = new Input();
        this.mClient = new OkHttpClient();
    }

    public WSApi id(String id) {
        input.id = id;
        return this;
    }

    public WSApi type(String type) {
        input.type = type;
        return this;
    }

    public WSApi url(String url) {
        input.url = url;
        return this;
    }

    public WSApi body(String body) {
        input.body = body;
        return this;
    }

    public WSApi header(Map<String, String> header) {
        input.header = header;
        return this;
    }

    public WSApi listener(OnFinishedWSApi listener) {
        input.listener = listener;
        return this;
    }

    private Type getType() {
        if (input.type.equalsIgnoreCase("GET"))
            return Type.GET;
        else if (input.type.equalsIgnoreCase("POST"))
            return Type.POST;
        else if (input.type.equalsIgnoreCase("PUT"))
            return Type.PUT;
        else if (input.type.equalsIgnoreCase("DELETE"))
            return Type.DELETE;
        else
            return null;
    }

    public void execute(){
        if (input.url == null)
            input.listener.onError(input.id, "No se ha indicado URL");
        else if (input.type == null)
            input.listener.onError(input.id, "No se ha indicado el tipo de petición");
        else {
            new AsyncJob.AsyncJobBuilder<Result>()
                    .doInBackground(new AsyncJob.AsyncAction<Result>() {
                        @Override
                        public Result doAsync() {

                            Result result = new Result();
                            result.id = input.id;

                            Request request = null;
                            switch (getType()) {
                                case GET:
                                    request = doGet(input.url, input.header);
                                    break;
                                case POST:
                                    request = doPost(input.url, input.header, input.body);
                                    break;
                                case PUT:
                                    request = doPut(input.url, input.header, input.body);
                                    break;
                                case DELETE:
                                    request = doDelete(input.url, input.header, input.body);
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
                                input.listener.onError(result.id, result.exception);
                            } else {
                                input.listener.onSuccess(result.id, result.header, result.data.replaceAll("\\p{C}", ""));
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

    private Request doPut(String url, Map<String, String> header, String json) {

        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder request = new Request.Builder();
        request.url(url);
        request.put(body);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        return request.build();
    }

    private Request doDelete(String url, Map<String, String> header, String json) {

        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder request = new Request.Builder();
        request.url(url);
        request.delete(body);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        return request.build();
    }
}