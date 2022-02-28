package com.example.nearby.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndStrategy.class)
public interface CreateRoomView extends MvpView {
    void activateRoom();

    void createQRForRoom(String mac);
}
