package com.example.nearby.presentation.presenter;

import android.util.Log;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.models.Room;
import com.example.nearby.network.AdminApi;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.view.MapView;
import com.github.terrakok.cicerone.Router;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MapPresenter extends BasePresenter<MapView> {
    private final Router router;
    private final UserApi userApi;
    private final AdminApi adminApi;
    private final CompositeDisposable disposables;

    public MapPresenter(Router router, UserApi userApi, AdminApi adminApi) {
        this.router = router;
        this.userApi = userApi;
        this.adminApi = adminApi;
        this.disposables = new CompositeDisposable();
    }

    public void leaveRoom(String roomId) {
        addDisposable(userApi.leaveRoom(roomId, getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ReturnToMainScreenDisposable()));
    }

    public void deleteRoom(String roomId) {
        addDisposable(adminApi.deleteRoom(roomId, getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ReturnToMainScreenDisposable()));
    }

    @Override
    protected void disconnect() {
        if (!disposables.isDisposed())
            disposables.clear();
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    private final class ReturnToMainScreenDisposable extends DisposableObserver<Room> {

        @Override
        public void onNext(Room room) {
            router.newRootScreen(new Screens.MainScreen());
        }

        @Override
        public void onError(Throwable e) {
            getViewState().showError(String.format("Ошибка при выходе/удалении комнаты - %s", e.getLocalizedMessage()));
        }

        @Override
        public void onComplete() {

        }
    }
}
