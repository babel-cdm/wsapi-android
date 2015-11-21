package library.webserviceapi;

import java.util.Map;

/**
 * Created by borja.velasco on 13/08/2015.
 */
public class RequestParams {
    String id;
    WSApi.Type type;
    String url;
    String body;
    String file;
    OnFinishedWSApi listener;
    Map<String, String> header;
    Map<String, String> urlParams;
    int secondsTimeout;

    public int getSecondsTimeout() {
        return secondsTimeout;
    }

    public RequestParams setSecondsTimeout(int secondsTimeout) {
        this.secondsTimeout = secondsTimeout;
        return this;
    }

    public RequestParams setUrlParams(Map<String, String> urlParams) {
        this.urlParams = urlParams;
        return this;
    }

    public RequestParams setId(String id) {
        this.id = id;
        return this;
    }

    public RequestParams setType(WSApi.Type type) {
        this.type = type;
        return this;
    }

    public RequestParams setUrl(String url) {
        this.url = url;
        return this;
    }

    public RequestParams setBody(String body) {
        this.body = body;
        return this;
    }

    public RequestParams setFile(String file) {
        this.file = file;
        return this;
    }

    public RequestParams setListener(OnFinishedWSApi listener) {
        this.listener = listener;
        return this;
    }

    public RequestParams setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }
}
