package com.example.nearby.presentation.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.common.Screens;
import com.example.nearby.models.Room;
import com.example.nearby.network.AdminApi;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.view.CreateRoomView;
import com.github.terrakok.cicerone.Router;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.StompClient;

@InjectViewState
public class CreateRoomPresenter extends BasePresenter<CreateRoomView> {
    private final Router router;
    private final UserApi userApi;
    private final AdminApi adminApi;
    private final StompClient stompClient;
    private final CompositeDisposable disposables;

    public CreateRoomPresenter(Router router,
                               UserApi userApi,
                               AdminApi adminApi,
                               StompClient stompClient) {
        this.router = router;
        this.userApi = userApi;
        this.adminApi = adminApi;
        this.stompClient = stompClient;
        this.disposables = new CompositeDisposable();
    }

    public void createRoom() {
        addDisposable(userApi.createRoom(getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {
                    @Override
                    public void onNext(Room room) {
                        getViewState().createQRForRoom(room.getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().showError(String.format("Ошибка при создании комнаты - %s", e.getLocalizedMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }


    public void activateRoom(String roomId) {
        addDisposable(adminApi.activateRoom(roomId, getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {
                    @Override
                    public void onNext(Room room) {
                        stompClient.send(String.format("/room/activate/%s", roomId)).subscribe();
                        router.newRootScreen(new Screens.MapScreen(true, roomId));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().showError(String.format("Ошибка при активации комнаты - %s", e.getLocalizedMessage()));
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

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }
}
