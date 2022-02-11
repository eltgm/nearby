package com.example.nearby.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.presentation.view.JoinRoomView;
import com.github.terrakok.cicerone.Router;

@InjectViewState
public class JoinRoomPresenter extends BasePresenter<JoinRoomView> {
    private final Router router;

    public JoinRoomPresenter(Router router) {
        this.router = router;
    }

    public void leaveRoomWaitlist() {
        router.exit();
    }

    @Override
    protected void disconnect() {

    }
}
