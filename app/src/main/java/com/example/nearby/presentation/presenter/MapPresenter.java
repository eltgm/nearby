package com.example.nearby.presentation.presenter;

import static android.graphics.BitmapFactory.decodeResource;
import static org.apache.lucene.util.SloppyMath.haversinMeters;

import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.example.nearby.R;
import com.example.nearby.common.Screens;
import com.example.nearby.models.Coordinates;
import com.example.nearby.models.Room;
import com.example.nearby.models.User;
import com.example.nearby.network.AdminApi;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.view.MapView;
import com.github.terrakok.cicerone.Router;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MapPresenter extends BasePresenter<MapView> {
    private static final String MAP_TAG = "users_map_tag";
    private static final int DISTANCE_BETWEEN_USERS_IN_METERS = 15;

    private final Router router;
    private final UserApi userApi;
    private final AdminApi adminApi;
    private final Resources resources;
    private final CompositeDisposable disposables;
    private final Map<String, PlacemarkMapObject> drawnUsers = new HashMap<>();

    public MapPresenter(Router router, UserApi userApi, AdminApi adminApi, Resources resources) {
        this.router = router;
        this.userApi = userApi;
        this.adminApi = adminApi;
        this.resources = resources;
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

    public void updateUsersCoordinates(String roomId, MapObjectCollection mapObjectCollection, Location location) {
        String userId = getUserId();

        addDisposable(userApi.updateCoordinates(roomId, userId,
                new Coordinates(location.getLongitude(), location.getLatitude()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Room>() {

                    @Override
                    public void onNext(Room room) {
                        Log.d(MAP_TAG, "обновление координат");
                        List<User> users = room.getUsers();

                        checkLeftUsersAndDeletePlacemarks(users);
                        drawUsersIfNecessary(users, userId, location, mapObjectCollection);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().showError("Ошибка при обновлении координат - " + e.getLocalizedMessage());
                        router.newRootScreen(new Screens.MainScreen());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    private void drawUsersIfNecessary(List<User> users, String userId, @NonNull Location location, MapObjectCollection mapObjectCollection) {
        for (User user : users) {
            if (user.getLastCoordinates() == null) continue;

            String updatedUserId = user.getId();
            if (updatedUserId.equals(userId)) continue;

            Coordinates updatedUserLastCoordinates = user.getLastCoordinates();
            Double updatedUserLastCoordinatesLongitude = updatedUserLastCoordinates.getLongitude();
            Double updatedUserLastCoordinatesLatitude = updatedUserLastCoordinates.getLatitude();
            double distanceBetweenUsers = haversinMeters(location.getLatitude(), location.getLongitude()
                    , updatedUserLastCoordinatesLatitude, updatedUserLastCoordinatesLongitude);
            Point newUserPoint = new Point(updatedUserLastCoordinatesLatitude, updatedUserLastCoordinatesLongitude);

            if (drawnUsers.containsKey(updatedUserId)) {
                Log.d(MAP_TAG, String.format("Пользователь с id - %s уже отрисован", updatedUserId));
                PlacemarkMapObject userMark = drawnUsers.get(updatedUserId);
                if (distanceBetweenUsers <= DISTANCE_BETWEEN_USERS_IN_METERS) {
                    Log.d(MAP_TAG, String.format("Обновляем позицию пользователя - %s", updatedUserId));
                    userMark.setGeometry(newUserPoint);
                } else {
                    Log.d(MAP_TAG, String.format("Пользователь - %s находится на расстоянии %f", updatedUserId, distanceBetweenUsers));
                    mapObjectCollection.remove(userMark);
                    drawnUsers.remove(updatedUserId);
                }
            } else if (distanceBetweenUsers <= DISTANCE_BETWEEN_USERS_IN_METERS) {
                Log.d(MAP_TAG, String.format("Пользователя - %s нет. Рисуем", updatedUserId, distanceBetweenUsers));
                drawnUsers.put(updatedUserId, mapObjectCollection.addPlacemark(newUserPoint, ImageProvider.fromBitmap(decodeResource(resources, R.drawable.outline_account_circle_black_36))));
            }
            Log.d(MAP_TAG, String.format("Расстояние между пользователями - %f", distanceBetweenUsers));
        }
    }

    private void checkLeftUsersAndDeletePlacemarks(List<User> users) {
        List<String> usersIdToDelete = new ArrayList<>();
        for (Map.Entry<String, PlacemarkMapObject> stringPlacemarkMapObjectEntry : drawnUsers.entrySet()) {
            String drawnUserId = stringPlacemarkMapObjectEntry.getKey();
            boolean isDelete = true;
            for (User user : users) {
                if (user.getId().equals(drawnUserId)) {
                    isDelete = false;
                    break;
                }
            }

            if (isDelete) {
                usersIdToDelete.add(drawnUserId);
            }
        }
        for (String userId : usersIdToDelete) {
            drawnUsers.remove(userId);
        }
        usersIdToDelete.clear();
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
            router.newRootScreen(new Screens.MainScreen());
        }

        @Override
        public void onComplete() {

        }
    }
}
