package com.example.nearby.presentation.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.presenter.StartPresenter;
import com.example.nearby.presentation.view.StartView;
import com.github.terrakok.cicerone.Router;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class StartFragment extends MvpAppCompatFragment implements StartView {
    @Inject
    Router router;
    @Inject
    UserApi userApi;
    @InjectPresenter
    StartPresenter startPresenter;

    private StartFragment() {
    }

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @ProvidePresenter
    StartPresenter provideStartPresenter() {
        return new StartPresenter(router);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectStartFragment(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.bCreateRoom, R.id.bEnterRoom, R.id.bMyRooms})
    public void onCreateRoomClick(View button) {
        switch (button.getId()) {
            case R.id.bCreateRoom:
                moveToCreateRoomScreen();
                break;
            case R.id.bEnterRoom:
                moveToJoinRoomScreen();
                break;
            case R.id.bMyRooms:
                Toast.makeText(getContext(), "В разработке...", Toast.LENGTH_SHORT).show();
                //moveToMyRoomsScreen();
                break;
        }
    }

    @Override
    public void moveToCreateRoomScreen() {
        startPresenter.moveToCreateRoomScreen();
    }

    @Override
    public void moveToJoinRoomScreen() {
        startPresenter.moveToJoinRoomScreen();
    }

    @Override
    public void moveToMyRoomsScreen() {
        startPresenter.moveToMyRoomsScreen();
    }
}
