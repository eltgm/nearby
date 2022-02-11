package com.example.nearby.di;

import com.github.terrakok.cicerone.Cicerone;
import com.github.terrakok.cicerone.NavigatorHolder;
import com.github.terrakok.cicerone.Router;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class NavigationModule {
    private final Cicerone<Router> cicerone;

    public NavigationModule() {
        this.cicerone = Cicerone.create();
    }

    @Singleton
    @Provides
    public NavigatorHolder getNavigatorHolder() {
        return cicerone.getNavigatorHolder();
    }

    @Singleton
    @Provides
    public Router getRouter() {
        return cicerone.getRouter();
    }
}
