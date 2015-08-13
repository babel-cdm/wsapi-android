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

                            Request request = doRequest();

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
                                params.listener.onError(result.id, result.exception);
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
        request.url(params.url);

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