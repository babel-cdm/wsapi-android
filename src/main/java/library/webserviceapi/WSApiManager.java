package library.webserviceapi;

public class WSApiManager {

    static WSApiComponent mComponent = DaggerWSApiComponent.builder()
            .wSApiModule(new WSApiModule()).build();

    public static WSApi getWSApi() {
        WSApi wsApi = mComponent.provideWSApi();
        return wsApi;
    }

    public static MockWSApi getMockWSApi() {
        MockWSApi mock = mComponent.provideMockWSApi();
        return mock;
    }
}
