package com.example.nearby.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.models.Room;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.view.JoinRoomView;
import com.github.terrakok.cicerone.Router;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class JoinRoomPresenter extends BasePresenter<JoinRoomView> {
    private final Router router;
    private final UserApi userApi;
    private final CompositeDisposable disposables;

    public JoinRoomPresenter(Router router, UserApi userApi) {
        this.router = router;
        this.userApi = userApi;
        this.disposables = new CompositeDisposable();
    }

    public void moveToMap(String roomId) {
        router.newRootScreen(new Screens.MapScreen(false, roomId));
    }

    public void leaveRoomWaitlist(String roomId) {
        addDisposable(userApi.leaveRoom(roomId, getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {
                    @Override
                    public void onNext(Room room) {
                        router.newRootScreen(new Screens.MainScreen());
                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().showError(String.format("Ошибка при выходе из комнаты - %s", e.getLocalizedMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Override
    protected void disconnect() {
        if (!disposables.isDisposed())
            disposables.clear();
    }

    public void joinRoom(String roomId) {
        addDisposable(userApi.enterRoom(roomId, getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {

            @Override
            public void onNext(Room room) {
                getViewState().joinWaitlist();
            }

            @Override
            public void onError(Throwable e) {
                getViewState().showError(String.format("Ошибка при входе в комнату - %s", e.getLocalizedMessage()));
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }
}
