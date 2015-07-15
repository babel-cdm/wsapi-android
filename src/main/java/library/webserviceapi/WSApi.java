package library.webserviceapi;

import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import library.utils.async.AsyncJob;

@SuppressWarnings("unused")
public class WSApi {

    class Result {
        String id;
        String data;
        String exception;
    }

    OkHttpClient mClient = new OkHttpClient();

    public void setPinningCertificate(String hostname, String publicKey) {
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(hostname, publicKey) //"sha1/BOGUSPIN")
                .build();

        mClient.setCertificatePinner(certificatePinner);
    }

    public void get(final String url, final OnFinishedWSApi listener) {
        get(null, url, listener);
    }

    public void get(final String id, final String url, final OnFinishedWSApi listener) {

        new AsyncJob.AsyncJobBuilder<Result>()
            .doInBackground(new AsyncJob.AsyncAction<Result>() {
                @Override
                public Result doAsync() {
                    Result result = new Result();
                    result.id = id;
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    try {
                        Response response = mClient.newCall(request).execute();
                        result.data = response.body().string();

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
                        listener.onSuccess(result.id, result.data.replaceAll("\\p{C}", ""));
                    }
                }

            }).create().start();
    }
}