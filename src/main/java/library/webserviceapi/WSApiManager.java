package library.webserviceapi;

/*
* This class has been deprecated as Wsapi shouldn't be singleton any more to prevent
* requests to overlap
*/
public class WSApiManager {

    private static WSApi wsapi = null;
    private static MockWSApi mock = null;

    public static WSApi getWSApi() {
/*        if (wsapi == null) {
            wsapi = new WSApi();
        }
        return wsapi;*/

        return new WSApi();
    }

    public static MockWSApi getMockWSApi() {
/*        if (mock == null) {
            mock = new MockWSApi();
        }
        return mock;*/

        return new MockWSApi();
    }
}
