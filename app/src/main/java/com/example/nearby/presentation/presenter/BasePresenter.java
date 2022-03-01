package com.example.nearby.presentation.presenter;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.SharedPreferences;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.example.nearby.di.App;

import java.util.UUID;


public abstract class BasePresenter<View extends MvpView> extends MvpPresenter<View> {
    private final String USER_ID_KEY = "USER_ID";

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disconnect();
    }

    protected abstract void disconnect();

    public String getUserId() {
        SharedPreferences idStorage
                = getDefaultSharedPreferences(App.INSTANCE);
        String userId = idStorage.getString(USER_ID_KEY, null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            SharedPreferences.Editor edit = idStorage.edit();
            edit.putString(USER_ID_KEY, userId);
            edit.commit();
        }

        return userId;
    }
}
