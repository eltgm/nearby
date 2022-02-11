package com.example.nearby.presentation.presenter;

import static com.example.nearby.common.Screens.CreateRoomScreen;
import static com.example.nearby.common.Screens.JoinRoomScreen;
import static com.example.nearby.common.Screens.MyRoomsScreen;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.presentation.view.StartView;
import com.github.terrakok.cicerone.Router;

@InjectViewState
public class StartPresenter extends BasePresenter<StartView> {
    private final Router router;

    public StartPresenter(Router router) {
        this.router = router;
    }

    public void moveToCreateRoomScreen() {
        router.navigateTo(new CreateRoomScreen());
    }

    public void moveToJoinRoomScreen() {
        router.navigateTo(new JoinRoomScreen());
    }

    public void moveToMyRoomsScreen() {
        router.navigateTo(new MyRoomsScreen());
    }

    @Override
    protected void disconnect() {

    }
}
