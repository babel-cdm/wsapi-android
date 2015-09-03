package library.webserviceapi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WSApiModule {

    @Provides
    @Singleton
    WSApi provideWSApi() {
        return new WSApi();
    }

    @Provides
    @Singleton
    MockWSApi provideMOckWSApi() {
        return new MockWSApi();
    }
}
