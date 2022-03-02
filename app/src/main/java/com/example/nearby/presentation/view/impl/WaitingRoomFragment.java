package com.example.nearby.presentation.view.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.presenter.JoinRoomPresenter;
import com.example.nearby.presentation.view.JoinRoomView;
import com.github.terrakok.cicerone.Router;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WaitingRoomFragment extends MvpAppCompatFragment implements JoinRoomView {
    private String roomId;

    @Inject
    Router router;
    @Inject
    UserApi userApi;
    @BindView(R.id.scanner_view)
    CodeScannerView scannerView;
    @BindView(R.id.bLeaveRoomWaitlist)
    Button bLeaveRoomWaitlist;

    private WaitingRoomFragment() {
    }

    @InjectPresenter
    JoinRoomPresenter joinRoomPresenter;

    @ProvidePresenter
    JoinRoomPresenter provideJoinRoomPresenter() {
        return new JoinRoomPresenter(router, userApi);
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
        startScanning();
    }

    @Override
    @OnClick(R.id.bLeaveRoomWaitlist)
    public void leaveRoom() {
        joinRoomPresenter.leaveRoomWaitlist(roomId);
        this.roomId = null;
    }

    private void startScanning() {
        CodeScanner mCodeScanner = new CodeScanner(getContext(), scannerView);
        mCodeScanner.setDecodeCallback(result -> getActivity().runOnUiThread(() -> {
            this.roomId = result.getText();
            joinRoomPresenter.joinRoom(roomId);
            mCodeScanner.releaseResources();
        }));
        mCodeScanner.startPreview();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void joinWaitlist() {
        bLeaveRoomWaitlist.setVisibility(View.VISIBLE);
        scannerView.setVisibility(View.GONE);
    }
}
