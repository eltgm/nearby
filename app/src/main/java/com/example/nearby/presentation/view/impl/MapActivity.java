package com.example.nearby.presentation.view.impl;

import static android.graphics.BitmapFactory.decodeResource;
import static com.example.nearby.common.Screens.MapScreen.IS_USER_ADMIN;
import static com.example.nearby.common.Screens.MapScreen.ROOM_ID;
import static org.apache.lucene.util.SloppyMath.haversinMeters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.nearby.R;
import com.example.nearby.di.App;
import com.example.nearby.models.Coordinates;
import com.example.nearby.models.Room;
import com.example.nearby.models.User;
import com.example.nearby.network.AdminApi;
import com.example.nearby.network.UserApi;
import com.example.nearby.presentation.presenter.MapPresenter;
import com.example.nearby.presentation.view.MapView;
import com.github.terrakok.cicerone.Navigator;
import com.github.terrakok.cicerone.NavigatorHolder;
import com.github.terrakok.cicerone.Router;
import com.github.terrakok.cicerone.androidx.AppNavigator;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends MvpAppCompatActivity implements MapView, UserLocationObjectListener {
    private final Navigator navigator
            = new AppNavigator(this, -1);
    private boolean isUserAdmin = false;
    private String roomId;
    private MapObjectCollection mapObjectCollection;
    private final Map<String, PlacemarkMapObject> drawnUsers = new HashMap<>();

    @Inject
    Router router;
    @Inject
    NavigatorHolder navigatorHolder;
    @Inject
    UserApi userApi;
    @Inject
    AdminApi adminApi;

    @BindView(R.id.mapView)
    com.yandex.mapkit.mapview.MapView mapView;
    @InjectPresenter
    MapPresenter mapPresenter;
    private UserLocationLayer userLocationLayer;

    @ProvidePresenter
    MapPresenter provideMapPresenter() {
        return new MapPresenter(router, userApi, adminApi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("a2b719b5-bc4d-454d-945e-cfe787365532");
        MapKitFactory.initialize(this);
        App.INSTANCE.getAppComponent().injectMapActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
    }

    private void initView() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.isUserAdmin = extras.getBoolean(IS_USER_ADMIN, false);
            this.roomId = extras.getString(ROOM_ID, "");
        }

        ButterKnife.bind(this);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(new Point(59.945933, 30.320045), 10, 0, 0));

        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);
        this.mapObjectCollection = mapView.getMap()
                .getMapObjects();

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,
                10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        String userId = mapPresenter.getUserId();
                        DisposableObserver<Room> disposableObserver = userApi.updateCoordinates(roomId, userId,
                                new Coordinates(location.getLongitude(), location.getLatitude()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<Room>() {

                                    @Override
                                    public void onNext(Room room) {
                                        System.out.println("room = " + room);

                                        List<User> users = room.getUsers();

                                        checkLeftUsersAndDeletePlacemarks(users);
                                        drawUsersIfNecessary(users, userId, location);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.getLocalizedMessage();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {

                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }
                });
    }

    private void drawUsersIfNecessary(List<User> users, String userId, @NonNull Location location) {
        for (User user : users) {
            String updatedUserId = user.getId();
            if (updatedUserId.equals(userId)) continue;

            Coordinates updatedUserLastCoordinates = user.getLastCoordinates();
            Double updatedUserLastCoordinatesLongitude = updatedUserLastCoordinates.getLongitude();
            Double updatedUserLastCoordinatesLatitude = updatedUserLastCoordinates.getLatitude();
            double distanceBetweenUsers = haversinMeters(location.getLatitude(), location.getLongitude()
                    , updatedUserLastCoordinatesLatitude, updatedUserLastCoordinatesLongitude);
            Point newUserPoint = new Point(updatedUserLastCoordinatesLatitude, updatedUserLastCoordinatesLongitude);

            if (drawnUsers.containsKey(updatedUserId)) {
                PlacemarkMapObject userMark = drawnUsers.get(updatedUserId);
                if (distanceBetweenUsers <= 10) {
                    userMark.setGeometry(newUserPoint);
                } else {
                    mapObjectCollection.remove(userMark);
                    drawnUsers.remove(updatedUserId); //TODO проверить, что удалился
                }
            } else if (distanceBetweenUsers <= 10) {
                drawnUsers.put(updatedUserId, mapObjectCollection.addPlacemark(newUserPoint, ImageProvider.fromBitmap(decodeResource(getResources(), R.drawable.outline_account_circle_black_36))));
            }
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
    public void leaveRoom() {
        mapPresenter.leaveRoom(roomId);
    }

    @Override
    public void deleteRoom() {
        mapPresenter.deleteRoom(roomId);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(MapActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResumeFragments() {
        super.onResume();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    @OnClick(R.id.bCloseRoom)
    public void closeRoomButtonListener() {
        if (isUserAdmin) {
            deleteRoom();
        } else {
            leaveRoom();
        }
    }
}
