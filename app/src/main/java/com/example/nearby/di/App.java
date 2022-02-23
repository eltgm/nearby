package com.example.nearby.di;

import android.app.Application;

public class App extends Application {
    public static App INSTANCE;
    private static AppComponent component;

    public AppComponent getAppComponent() {
        if (component == null) {
            component = DaggerAppComponent.builder()
                    .networkModule(new NetworkModule("http://192.168.50.13:8080"))
                    .build();
        }

        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}