package com.example.nearby.presentation.view.impl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA
                },
                1);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "Permission denied.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    openStartScreen();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
