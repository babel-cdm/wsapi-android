package library.webserviceapi.utils;

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
