package com.example.nearby.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.presentation.view.CreateRoomView;
import com.github.terrakok.cicerone.Router;

@InjectViewState
public class CreateRoomPresenter extends BasePresenter<CreateRoomView> {
    private final Router router;

    public CreateRoomPresenter(Router router) {
        this.router = router;
    }

    public void activateRoom() {
        router.newRootScreen(new Screens.MapScreen());
    }

    @Override
    protected void disconnect() {

    }
}
