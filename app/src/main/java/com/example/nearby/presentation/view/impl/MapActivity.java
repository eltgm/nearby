package com.example.nearby.presentation.view.impl;

import static com.example.nearby.common.Screens.MapScreen.IS_USER_ADMIN;
import static com.example.nearby.common.Screens.MapScreen.ROOM_ID;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends MvpAppCompatActivity implements MapView, UserLocationObjectListener {
    private final Navigator navigator
            = new AppNavigator(this, -1);
    private boolean isUserAdmin = false;
    private String roomId;

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
        mapView.getMap()
                .getMapObjects()
                .addClusterizedPlacemarkCollection(new ClusterListener() {
            @Override
            public void onClusterAdded(@NonNull Cluster cluster) {
                System.out.println("cluster = " + cluster);
            }
        })
                .addPlacemark(new Point(55.7475, 48.74), ImageProvider.fromBitmap(drawSimpleBitmap("Андрей")));
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

    public Bitmap drawSimpleBitmap(String number) {
        int picSize = 250;
        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
        // отрисовка текста
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(number, picSize / 2,
                picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
        return bitmap;
    }
}
