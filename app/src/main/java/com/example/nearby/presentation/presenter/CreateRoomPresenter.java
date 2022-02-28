package com.example.nearby.presentation.presenter;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.yandex.runtime.Runtime.getApplicationContext;

import android.content.SharedPreferences;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.di.App;
import com.example.nearby.models.Room;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.view.CreateRoomView;
import com.example.nearby.presentation.view.impl.MainActivity;
import com.github.terrakok.cicerone.Router;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class CreateRoomPresenter extends BasePresenter<CreateRoomView> {
    private final Router router;
    private final UserApi userApi;
    private final CompositeDisposable disposables;

    public CreateRoomPresenter(Router router, UserApi userApi) {
        this.router = router;
        this.userApi = userApi;
        this.disposables = new CompositeDisposable();
    }

    public void createRoom() {
        String userId = getUserId();

        addDisposable(userApi.createRoom(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {
                    @Override
                    public void onNext(Room room) {
                        getViewState().createQRForRoom(room.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private String getUserId() {
        SharedPreferences idStorage
                = getDefaultSharedPreferences(App.INSTANCE);
        String userId = idStorage.getString("USER_ID", null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            SharedPreferences.Editor edit = idStorage.edit();
            edit.putString("USER_ID", userId);
            edit.commit();
        }

        return userId;
    }

    public void activateRoom() {
        router.newRootScreen(new Screens.MapScreen());
    }

    @Override
    protected void disconnect() {
        if (!disposables.isDisposed())
            disposables.clear();
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }
}
