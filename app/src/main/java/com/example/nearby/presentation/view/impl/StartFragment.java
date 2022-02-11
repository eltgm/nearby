package com.example.nearby.presentation.view.impl;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.network.RoomsApi;
import com.example.nearby.presentation.presenter.StartPresenter;
import com.example.nearby.presentation.view.StartView;
import com.github.terrakok.cicerone.Router;

import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Single;
import rx.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class StartFragment extends MvpAppCompatFragment implements StartView {
    @Inject
    Router router;
    @Inject
    RoomsApi roomsApi;
    @Inject
    StompClient stompClient;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @InjectPresenter
    StartPresenter startPresenter;

    private StartFragment() {
    }

    @ProvidePresenter
    StartPresenter provideStartPresenter() {
        return new StartPresenter(router);
    }

    public static StartFragment newInstance() {
        return new StartFragment();
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

        stompClient.connect();

        stompClient.topic("/topic/greetings").subscribe(topicMessage -> {
            Log.d("NEARBY", topicMessage.getPayload());
        });

        stompClient.send("/topic/hello", "My first STOMP message!").subscribe();

        return view;
    }

    @OnClick({R.id.bCreateRoom, R.id.bEnterRoom, R.id.bMyRooms})
    public void onCreateRoomClick(View button) {
        Editable text = etUserName.getText();
        if (text.length() > 0) {
            switch (button.getId()) {
                case R.id.bCreateRoom:
                    moveToCreateRoomScreen();
                    break;
                case R.id.bEnterRoom:
                    moveToJoinRoomScreen();
                    break;
                case R.id.bMyRooms:
                    moveToMyRoomsScreen();
                    break;
            }

        } else {
            etUserName.requestFocus();
            etUserName.setError("Введите имя!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stompClient.disconnect();
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
