package library.webserviceapi;

import android.content.Context;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.Map;

import library.utils.async.AsyncJob;
import library.webserviceapi.utils.FileReader;
import library.webserviceapi.utils.MockWSResponse;

public class MockWSApi {

    private MockWebServer mServer;
    private Dispatcher mDispatcher;
        private Map<String, MockWSResponse>  mResponse;
//    private MockWSResponse mResponse;

    public MockWSApi init(final Map<String, MockWSResponse> response, final Context context) {
        this.mServer = new MockWebServer();
        this.mResponse = response;
        this.mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String res = mResponse.get(request.getPath()).getResponse();
                if (res != null) {
                    if (mResponse.get(request.getPath()).getType().equals(MockWSResponse.ResponseType.PATH))
                        return new MockResponse().setResponseCode(200).setBody(FileReader.readFromfile(context, res));
                    else if (mResponse.get(request.getPath()).getType().equals(MockWSResponse.ResponseType.FLAT))
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