package library.webserviceapi;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import library.webserviceapi.exception.EmptyTypeRequestException;
import library.webserviceapi.exception.EmptyURLException;
import library.webserviceapi.utils.AsyncJob;
import library.webserviceapi.utils.MySSLSocketFactory;

@SuppressWarnings("unused")
public class WSApi {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private static final long DEFAULT_SECONDS_TIMEOUT = 60;

    public enum Type {GET, POST, PUT, DELETE}

    private OkHttpClient mClient;
    private RequestParams params;

    public WSApi setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public WSApi() {
        this.mClient = new OkHttpClient();
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            this.mClient.setSslSocketFactory(new MySSLSocketFactory());
        }
    }

    class Result {
        String id;
        String data;
        Headers header;
        String exception;
        int code;
        boolean timeout;
    }

    public Response executeSync() throws EmptyURLException, EmptyTypeRequestException, IOException, SocketTimeoutException {
        if (params.url == null)
            throw new EmptyURLException();
        else if (params.type == null)
            throw new EmptyTypeRequestException();
        else {
            String result = "";

            if (params.getSecondsTimeout() != 0) {
                mClient.setConnectTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
                mClient.setReadTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
                mClient.setWriteTimeout(params.getSecondsTimeout(), TimeUnit.SECONDS);
            } else {
                mClient.setConnectTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
                mClient.setReadTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
                mClient.setWriteTimeout(DEFAULT_SECONDS_TIMEOUT, TimeUnit.SECONDS);
            }

            Request request = buildRequest();

            Response response = null;

            try {
                response = mClient.newCall(request).execute();
            } catch (SocketTimeoutException socketTimeout) {
                throw socketTimeout;
            } catch (final IOException e) {
                throw e;
            }

            return response;
        }
    }

    public void execute() {

        //Check if URL is included
        if (params.url == null) {
            params.listener.onError(params.id, "No se ha indicado URL");

            //Check if type is included
        } else if (params.type == null) {
            params.listener.onError(params.id, "No se ha indicado el tipo de petición");
        } else {
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

                            Request request = buildRequest();

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
//                                Log.w(params.listener.getClass().getSimpleName(), result.id.toString());
                            } else if (result.exception != null) {
                                params.listener.onException(result.id, result.exception.toString());
                                Log.e(params.listener.getClass().getSimpleName(), result.exception);
                            } else if (!(result.code >= 200 && result.code < 300)) {
                                params.listener.onError(result.id, result.data);
                                Log.e(params.listener.getClass().getSimpleName(), result.data);
                            } else {
                                String data = result.data.replaceAll("\\p{C}", "");
                                params.listener.onSuccess(result.id, result.header, data);
                                Log.d(params.listener.getClass().getSimpleName(), data);
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

    private Request buildRequest() {
        Request.Builder request = new Request.Builder();
        if (params.urlParams == null) {
            request.url(params.url);
        } else {
            String url = params.url;
            Iterator it = params.urlParams.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                if(url.contains("?")){
                    url += "&" + e.getKey() + "=" + e.getValue();
                }else{
                    url += "?" + e.getKey() + "=" + e.getValue();
                }
            }

            request.url(url);
        }
        if (!params.type.equals(params.type.GET)) {
            RequestBody body;
            body = RequestBody.create(JSON, params.body);
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

    public void uploadFile(final Context context, final File file) {
        new AsyncJob.AsyncJobBuilder<Result>()
                .doInBackground(new AsyncJob.AsyncAction<Result>() {
                    @Override
                    public Result doAsync() {
                        final Result result = new Result();
                        result.id = params.id;

                        Future uploading = Ion.with(context)
                                .load(params.url)
                                .setTimeout(params.secondsTimeout * 3)
                                .setHeader("x-access-token", params.header.get("x-access-token"))
                                .setMultipartFile("file", file)
                                .asString()
                                .withResponse()
                                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                                    @Override
                                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> resultCall) {
                                        if (e != null) {
                                            result.exception = e.toString();
                                            params.listener.onError(result.id, e.toString());
                                        } else {
                                            params.listener.onSuccess(result.id, result.header, resultCall.getResult().toString().replaceAll("\\p{C}", ""));
                                        }
                                    }
                                });

                        return result;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Result>() {
                    @Override
                    public void onResult(Result result) {
                        // TODO ...
                    }

                }).create().start();
    }
}