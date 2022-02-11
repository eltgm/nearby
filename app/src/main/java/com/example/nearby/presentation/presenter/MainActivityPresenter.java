package com.example.nearby.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.presentation.view.MainActivityView;
import com.github.terrakok.cicerone.Router;

@InjectViewState
public class MainActivityPresenter extends BasePresenter<MainActivityView> {
    private final Router router;

    public MainActivityPresenter(Router router) {
        this.router = router;
    }

    public void openStartScreen() {
        router.newRootScreen(new Screens.StartScreen());
    }

    @Override
    protected void disconnect() {

    }
}
