package com.example.nearby.di;

import static ua.naiksoftware.stomp.Stomp.ConnectionProvider.OKHTTP;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

@Module
public class SocketModule {
    private final String baseUrl;

    public SocketModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    public StompClient provideStompClient() {
        return Stomp.over(OKHTTP, baseUrl);
    }
}
