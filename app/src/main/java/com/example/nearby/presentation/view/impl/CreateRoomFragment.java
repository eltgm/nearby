package com.example.nearby.presentation.view.impl;

import static com.google.zxing.BarcodeFormat.QR_CODE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.presenter.CreateRoomPresenter;
import com.example.nearby.presentation.view.CreateRoomView;
import com.github.terrakok.cicerone.Router;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateRoomFragment extends MvpAppCompatFragment implements CreateRoomView {
    @BindView(R.id.ivQR)
    ImageView ivQR;
    @BindView(R.id.bActivate)
    Button bActivate;
    @Inject
    Router router;
    @Inject
    UserApi userApi;

    private CreateRoomFragment() {
    }

    @InjectPresenter
    CreateRoomPresenter createRoomPresenter;

    @ProvidePresenter
    CreateRoomPresenter provideCreateRoomPresenter() {
        return new CreateRoomPresenter(router, userApi);
    }

    public static CreateRoomFragment newInstance() {
        CreateRoomFragment fragment = new CreateRoomFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectCreateRoomFragment(this);
        super.onCreate(savedInstanceState);
        createRoomPresenter.createRoom();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_room_create, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {

    }

    @Override
    @OnClick(R.id.bActivate)
    public void activateRoom() {
        createRoomPresenter.activateRoom();
    }

    @Override
    public void createQRForRoom(String mac) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(mac, QR_CODE, 1000, 1000);
            ivQR.setImageBitmap(bitmap);
            bActivate.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }
}
