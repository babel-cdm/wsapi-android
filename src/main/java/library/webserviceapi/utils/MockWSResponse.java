package library.webserviceapi.utils;

import java.util.Map;

/**
 * Created by borja.velasco on 14/08/2015.
 */
public class MockWSResponse {

    public enum ResponseType {FLAT, PATH}

    String response;
    ResponseType type;

    public MockWSResponse(String response, ResponseType type) {
        this.response = response;
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
