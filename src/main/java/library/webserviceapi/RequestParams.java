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
    OnFinishedWSApi listener;
    Map<String, String> header;

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

    public RequestParams setListener(OnFinishedWSApi listener) {
        this.listener = listener;
        return this;
    }

    public RequestParams setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }
}
