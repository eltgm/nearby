package com.example.nearby.di;

import com.example.nearby.presentation.view.impl.CreateRoomFragment;
import com.example.nearby.presentation.view.impl.MainActivity;
import com.example.nearby.presentation.view.impl.MapActivity;
import com.example.nearby.presentation.view.impl.MyRoomsFragment;
import com.example.nearby.presentation.view.impl.StartFragment;
import com.example.nearby.presentation.view.impl.WaitingRoomFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NavigationModule.class, NetworkModule.class, SocketModule.class})
public interface AppComponent {
    void injectMainActivity(MainActivity mainActivity);

    void injectStartFragment(StartFragment startFragment);

    void injectMyRoomsFragment(MyRoomsFragment myRoomsFragment);

    void injectCreateRoomFragment(CreateRoomFragment createRoomFragment);

    void injectJoinRoomFragment(WaitingRoomFragment waitingRoomFragment);

    void injectMapActivity(MapActivity mapActivity);
}
