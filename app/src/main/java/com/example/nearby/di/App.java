package com.example.nearby.di;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

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
        MapKitFactory.setApiKey("a2b719b5-bc4d-454d-945e-cfe787365532");
    }
}