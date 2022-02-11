package com.example.nearby.presentation.view.impl;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.presentation.presenter.MainActivityPresenter;
import com.example.nearby.presentation.view.MainActivityView;
import com.github.terrakok.cicerone.Navigator;
import com.github.terrakok.cicerone.NavigatorHolder;
import com.github.terrakok.cicerone.Router;
import com.github.terrakok.cicerone.androidx.AppNavigator;

import javax.inject.Inject;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView {
    private final Navigator navigator
            = new AppNavigator(this, R.id.nav_host_fragment);

    @Inject
    Router router;
    @Inject
    NavigatorHolder navigatorHolder;
    @InjectPresenter
    MainActivityPresenter mainActivityPresenter;

    @ProvidePresenter
    MainActivityPresenter provideMainActivityPresenter() {
        return new MainActivityPresenter(router);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectMainActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openStartScreen();
    }

    @Override
    protected void onResumeFragments() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    @Override
    public void openStartScreen() {
        mainActivityPresenter.openStartScreen();
    }
}
