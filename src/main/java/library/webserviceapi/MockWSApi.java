package library.webserviceapi;

import android.content.Context;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;

import library.utils.async.AsyncJob;
import library.webserviceapi.utils.FileReader;
import library.webserviceapi.utils.MockWSResponse;

public class MockWSApi {

    private MockWebServer mServer;
    private Dispatcher mDispatcher;
    //    private Map<String, String>  mResponse;
    private MockWSResponse mResponse;

/*    public MockWSApi init(Map<String, String> response, final Context context) {
        this.mServer = new MockWebServer();
        this.mResponse = response;
        this.mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String res = mResponse.get(request.getPath());
                if (res != null) {
                    return new MockResponse().setResponseCode(200).setBody(FileReader.readFromfile(context, res));
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        return this;
    }*/

    public MockWSApi init(final MockWSResponse response, final Context context) {
        this.mServer = new MockWebServer();
        this.mResponse = response;
        this.mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String res = mResponse.getResponses().get(request.getPath());
                if (res != null) {
                    if (response.getType().equals(MockWSResponse.ResponseType.PATH))
                        return new MockResponse().setResponseCode(200).setBody(FileReader.readFromfile(context, res));
                    else if (response.getType().equals(MockWSResponse.ResponseType.FLAT))
                        return new MockResponse().setResponseCode(200).setBody(res);

                }
                return new MockResponse().setResponseCode(404);
            }
        };

        return this;
    }

    public String getMockUrl() {
        return mServer.getUrl("/").toString();
    }

    public void start(final OnMockWSApi listener) {
        new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() {
                        try {
                            mServer.setDispatcher(mDispatcher);
                            mServer.start();
                            listener.onMockSuccess();
                        } catch (final IOException e) {
                            listener.onMockError();
                        }

                        return true;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                    }

                }).create().start();
    }
}