package library.webserviceapi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {WSApiModule.class})
public interface WSApiComponent {

    WSApi provideWSApi();
    MockWSApi provideMockWSApi();
}
