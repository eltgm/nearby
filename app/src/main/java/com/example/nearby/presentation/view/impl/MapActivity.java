package com.example.nearby.presentation.view.impl;

import static com.example.nearby.common.Screens.MapScreen.IS_USER_ADMIN;
import static com.example.nearby.common.Screens.MapScreen.ROOM_ID;

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
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends MvpAppCompatActivity implements MapView, UserLocationObjectListener {
    private final Navigator navigator
            = new AppNavigator(this, -1);
    private boolean isUserAdmin = false;
    private boolean isFirstRender = true;
    private String roomId;
    private Map usersMap;
    private MapObjectCollection mapObjectCollection;
    private LocationListener locationListener;
    private LocationManager mLocationManager;

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
        return new MapPresenter(router, userApi, adminApi, getResources());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.initialize(this);
        App.INSTANCE.getAppComponent().injectMapActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initView();
        initMap();
    }

    private void initView() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.isUserAdmin = extras.getBoolean(IS_USER_ADMIN, false);
            this.roomId = extras.getString(ROOM_ID, "");
        }

        ButterKnife.bind(this);
    }

    private void initMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        this.usersMap = mapView.getMap();
        usersMap.setRotateGesturesEnabled(false);

        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);
        this.mapObjectCollection = usersMap.getMapObjects();

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (isFirstRender) {
                    usersMap.move(
                            new CameraPosition(
                                    new Point(location.getLatitude(), location.getLongitude()), 18, 0, 0
                            )
                    );
                    isFirstRender = false;
                }
                mapPresenter.updateUsersCoordinates(roomId, mapObjectCollection, location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                System.out.println("provider = " + provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                System.out.println("provider = " + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("provider = " + provider);
            }
        };
        mLocationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 10000, 0, this.locationListener);
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
        mLocationManager.removeUpdates(locationListener);
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
                new PointF((float) (mapView.getWidth() * 0.4), (float) (mapView.getHeight() * 0.4)),
                new PointF((float) (mapView.getWidth() * 0.4), (float) (mapView.getHeight() * 0.73)));
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
