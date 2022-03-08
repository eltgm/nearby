package com.example.nearby.presentation.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.presentation.view.MyRoomsView;

import butterknife.ButterKnife;

public class MyRoomsFragment extends MvpAppCompatFragment implements MyRoomsView {
    private MyRoomsFragment() {
    }

    public static MyRoomsFragment newInstance() {
        return new MyRoomsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectMyRoomsFragment(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_room_join, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {

    }

    @Override
    public void joinRoom(String id) {

    }

    @Override
    public void deleteRoom(String id, boolean isAdmin) {

    }
}
