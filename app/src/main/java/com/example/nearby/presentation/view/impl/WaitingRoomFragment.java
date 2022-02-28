package com.example.nearby.presentation.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.presentation.presenter.JoinRoomPresenter;
import com.example.nearby.presentation.view.JoinRoomView;
import com.github.terrakok.cicerone.Router;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WaitingRoomFragment extends MvpAppCompatFragment implements JoinRoomView {
    @Inject
    Router router;

    private WaitingRoomFragment() {
    }

    @InjectPresenter
    JoinRoomPresenter joinRoomPresenter;

    @ProvidePresenter
    JoinRoomPresenter provideJoinRoomPresenter() {
        return new JoinRoomPresenter(router);
    }

    public static WaitingRoomFragment newInstance() {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectJoinRoomFragment(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_waiting_room, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {

    }

    @Override
    @OnClick(R.id.bLeaveRoomWaitlist)
    public void leaveRoom() {
        joinRoomPresenter.leaveRoomWaitlist();
    }
}
