package com.example.nearby.common;

import static com.example.nearby.common.ScreenKeys.CREATE_ROOM;
import static com.example.nearby.common.ScreenKeys.JOIN_ROOM;
import static com.example.nearby.common.ScreenKeys.MAIN;
import static com.example.nearby.common.ScreenKeys.MAP_ACTIVITY;
import static com.example.nearby.common.ScreenKeys.MY_ROOMS;
import static com.example.nearby.common.ScreenKeys.START;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.example.nearby.presentation.view.impl.MainActivity;
import com.example.nearby.presentation.view.impl.CreateRoomFragment;
import com.example.nearby.presentation.view.impl.JoinRoomFragment;
import com.example.nearby.presentation.view.impl.MapActivity;
import com.example.nearby.presentation.view.impl.MyRoomsFragment;
import com.example.nearby.presentation.view.impl.StartFragment;
import com.github.terrakok.cicerone.androidx.ActivityScreen;
import com.github.terrakok.cicerone.androidx.FragmentScreen;

public class Screens {
    public static final class StartScreen implements FragmentScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return START;
        }

        @Override
        public boolean getClearContainer() {
            return true;
        }

        @NonNull
        @Override
        public Fragment createFragment(@NonNull FragmentFactory fragmentFactory) {
            return StartFragment.newInstance();
        }
    }

    public static final class CreateRoomScreen implements FragmentScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return CREATE_ROOM;
        }

        @Override
        public boolean getClearContainer() {
            return true;
        }

        @NonNull
        @Override
        public Fragment createFragment(@NonNull FragmentFactory fragmentFactory) {
            return CreateRoomFragment.newInstance();
        }
    }

    public static final class JoinRoomScreen implements FragmentScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return JOIN_ROOM;
        }

        @Override
        public boolean getClearContainer() {
            return true;
        }

        @NonNull
        @Override
        public Fragment createFragment(@NonNull FragmentFactory fragmentFactory) {
            return JoinRoomFragment.newInstance();
        }
    }

    public static final class MyRoomsScreen implements FragmentScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return MY_ROOMS;
        }

        @Override
        public boolean getClearContainer() {
            return true;
        }

        @NonNull
        @Override
        public Fragment createFragment(@NonNull FragmentFactory fragmentFactory) {
            return MyRoomsFragment.newInstance();
        }
    }

    public static final class MainScreen implements ActivityScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return MAIN;
        }

        @Nullable
        @Override
        public Bundle getStartActivityOptions() {
            return null;
        }

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context) {
            return new Intent(context, MainActivity.class);
        }
    }

    public static final class MapScreen implements ActivityScreen {

        @NonNull
        @Override
        public String getScreenKey() {
            return MAP_ACTIVITY;
        }

        @Nullable
        @Override
        public Bundle getStartActivityOptions() {
            return null;
        }

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context) {
            return new Intent(context, MapActivity.class);
        }
    }
}
