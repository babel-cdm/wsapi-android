package library.webserviceapi.utils;

import java.util.Map;

/**
 * Created by borja.velasco on 14/08/2015.
 */
public class MockWSResponse {

    public enum ResponseType {FLAT, PATH}
    Map<String, String> responses;
    ResponseType type;

    public MockWSResponse(Map<String, String> responses, ResponseType type) {
        this.responses = responses;
        this.type = type;
    }

    public Map<String, String> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, String> responses) {
        this.responses = responses;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
