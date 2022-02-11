package com.example.nearby.presentation.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndStrategy.class)
public interface MyRoomsView extends MvpView {
    void joinRoom(String id);

    void deleteRoom(String id, boolean isAdmin);
}
