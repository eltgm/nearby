package com.example.nearby.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(SingleStateStrategy.class)
public interface StartView extends MvpView {
    void moveToCreateRoomScreen();

    void moveToJoinRoomScreen();

    void moveToMyRoomsScreen();
}
