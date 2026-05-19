package com.roombuddy.pgrentalapp.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.ui.pg.PgDetailActivity;
import com.roombuddy.pgrentalapp.utils.PgRepository;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class NearestPgMapActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 101;
    private static final float NEAR_DISTANCE_KM = 0.1f; // 100 meters

    private MapView mapView;
    private FusedLocationProviderClient locationClient;

    private Marker userMarker;
    private GeoPoint userPoint;
    private Polyline routeLine;

    private boolean fromPgDetail;
    private int focusPgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_nearest_pg_map);

        mapView = findViewById(R.id.map);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        fromPgDetail = getIntent().getBooleanExtra("from_pg_detail", false);
        focusPgId = getIntent().getIntExtra("pg_id", -1);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST
            );
            return;
        }

        startLiveLocation();
    }

    @SuppressLint("MissingPermission")
    private void startLiveLocation() {

        LocationRequest request = LocationRequest.create();
        request.setInterval(5000);
        request.setFastestInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                updateMap(location);
            }
        }
    };

    private void updateMap(Location location) {

        mapView.getOverlays().clear();

        // USER LOCATION
        userPoint = new GeoPoint(
                location.getLatitude(),
                location.getLongitude()
        );

        userMarker = new Marker(mapView);
        userMarker.setPosition(userPoint);
        userMarker.setTitle("Your Location");
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setIcon(getResources().getDrawable(R.drawable.ic_user_location));

        mapView.getController().setCenter(userPoint);

        List<PgModel> pgList = PgRepository.getPgList(this);
        boolean userNearAnyPg = false;

        for (PgModel pg : pgList) {

            if (pg.getLatitude() == 0 || pg.getLongitude() == 0) continue;

            GeoPoint pgPoint = new GeoPoint(
                    pg.getLatitude(),
                    pg.getLongitude()
            );

            float distanceKm = calculateDistanceKm(userPoint, pgPoint);

            Marker pgMarker = new Marker(mapView);
            pgMarker.setPosition(pgPoint);
            pgMarker.setTitle(pg.getName());
            pgMarker.setSubDescription(
                    String.format("Distance: %.2f km", distanceKm)
            );
            pgMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            pgMarker.setIcon(getResources().getDrawable(R.drawable.ic_pg_location));

            if (fromPgDetail && pg.getPgId() == focusPgId) {
                mapView.getController().setCenter(pgPoint);
                mapView.getController().setZoom(17.0);
                pgMarker.showInfoWindow();
                drawRouteLine(pgPoint);
            }

            pgMarker.setOnMarkerClickListener((marker, map) -> {
                if (!marker.isInfoWindowShown()) {
                    marker.showInfoWindow();
                    drawRouteLine(pgPoint);
                } else {
                    Intent intent = new Intent(this, PgDetailActivity.class);
                    intent.putExtra("pg_id", pg.getPgId());
                    startActivity(intent);
                }
                return true;
            });

            mapView.getOverlays().add(pgMarker);

            if (distanceKm <= NEAR_DISTANCE_KM) {
                userNearAnyPg = true;
            }
        }

        // 🔥 USER MARKER PRIORITY
        if (userNearAnyPg) {
            mapView.getOverlays().add(userMarker); // ON TOP
        } else {
            mapView.getOverlays().add(0, userMarker); // BELOW PG markers
        }

        mapView.invalidate();
    }

    private float calculateDistanceKm(GeoPoint user, GeoPoint pg) {
        float[] result = new float[1];
        Location.distanceBetween(
                user.getLatitude(),
                user.getLongitude(),
                pg.getLatitude(),
                pg.getLongitude(),
                result
        );
        return result[0] / 1000f;
    }

    private void drawRouteLine(GeoPoint pgPoint) {

        if (routeLine != null) {
            mapView.getOverlays().remove(routeLine);
        }

        routeLine = new Polyline();
        routeLine.setColor(Color.BLUE);
        routeLine.setWidth(6f);

        List<GeoPoint> points = new ArrayList<>();
        points.add(userPoint);
        points.add(pgPoint);

        routeLine.setPoints(points);
        mapView.getOverlays().add(routeLine);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.removeLocationUpdates(locationCallback);
    }
}
