package com.example.nearby.presentation.view.impl;

import static android.text.TextUtils.isEmpty;
import static com.google.zxing.BarcodeFormat.QR_CODE;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.network.AdminApi;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.presenter.CreateRoomPresenter;
import com.example.nearby.presentation.view.CreateRoomView;
import com.github.terrakok.cicerone.Router;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.naiksoftware.stomp.StompClient;

public class CreateRoomFragment extends MvpAppCompatFragment implements CreateRoomView {
    @BindView(R.id.ivQR)
    ImageView ivQR;
    @BindView(R.id.bActivate)
    Button bActivate;
    @Inject
    Router router;
    @Inject
    UserApi userApi;
    @Inject
    AdminApi adminApi;
    @Inject
    StompClient stompClient;
    @InjectPresenter
    CreateRoomPresenter createRoomPresenter;
    private String roomId;

    private CreateRoomFragment() {
    }

    public static CreateRoomFragment newInstance() {
        return new CreateRoomFragment();
    }

    @ProvidePresenter
    CreateRoomPresenter provideCreateRoomPresenter() {
        return new CreateRoomPresenter(router, userApi, adminApi, stompClient);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.INSTANCE.getAppComponent().injectCreateRoomFragment(this);
        super.onCreate(savedInstanceState);
        stompClient.connect();
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
        if (!isEmpty(roomId)) {
            createRoomPresenter.activateRoom(roomId);
        } else {
            Toast.makeText(this.getActivity(), "Комната не создана!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void createQRForRoom(String roomId) {
        this.roomId = roomId;

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(roomId, QR_CODE, 1000, 1000);
            ivQR.setImageBitmap(bitmap);
            bActivate.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), "Ошибка при создании QR! Попробуйте еще", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        stompClient.disconnect();
    }
}
