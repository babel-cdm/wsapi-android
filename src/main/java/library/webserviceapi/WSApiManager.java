package library.webserviceapi;

public class WSApiManager {

    private static WSApi wsapi = null;
    private static MockWSApi mock = null;

    public static WSApi getWSApi() {
        if (wsapi == null) {
            wsapi = new WSApi();
        }

        return wsapi;
    }

    public static MockWSApi getMockWSApi() {
        if (mock == null) {
            mock = new MockWSApi();
        }

        return mock;
    }
}
