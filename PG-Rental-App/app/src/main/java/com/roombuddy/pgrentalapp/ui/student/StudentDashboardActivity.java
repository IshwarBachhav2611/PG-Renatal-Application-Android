package com.roombuddy.pgrentalapp.ui.student;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.adapter.PgAdapter;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.ui.home.HomeActivity;
import com.roombuddy.pgrentalapp.ui.map.NearestPgMapActivity;
import com.roombuddy.pgrentalapp.ui.pg.PgDetailActivity;
import com.roombuddy.pgrentalapp.utils.PgRepository;
import com.roombuddy.pgrentalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private static final int LOCATION_REQ = 201;

    private EditText edtSearch;
    private RecyclerView recyclerPgList;
    private ImageButton btnFilter, btnMore;
    private BottomNavigationView bottomNavigation;
    private View layoutEmpty;
    private TextView txtTitle, txtGreeting;

    private PgAdapter adapter;
    private SessionManager sessionManager;
    private FusedLocationProviderClient locationClient;
    private Location userLocation;

    private String selectedLocation = null;
    private String addressKeyword = null;
    private Integer minRent = null;
    private Integer maxRent = null;

    private final List<PgModel> currentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Bind views
        edtSearch = findViewById(R.id.edtSearch);
        recyclerPgList = findViewById(R.id.recyclerPgList);
        btnFilter = findViewById(R.id.btnFilter);
        btnMore = findViewById(R.id.btnMore);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        txtTitle = findViewById(R.id.txtTitle);
        txtGreeting = findViewById(R.id.txtGreeting);

        sessionManager = new SessionManager(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        txtTitle.setText("RoomBuddy");
        String name = sessionManager.getUserName();
        txtGreeting.setText("Welcome, " + (name != null ? name : "User") + " 👋");

        recyclerPgList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PgAdapter(this, currentList, pg -> {
            if (pg.getPgId() <= 0) {
                Toast.makeText(this, "PG detail not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PgDetailActivity.class);
            intent.putExtra("pg_id", pg.getPgId());
            startActivity(intent);
        });
        recyclerPgList.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                applySearch();
            }
        });

        btnFilter.setOnClickListener(v -> showFilterDialog());

        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_map) {
                startActivity(new Intent(this, NearestPgMapActivity.class));
                return true;
            }
            if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, MyBookingsActivity.class));
                return true;
            }
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, StudentProfileActivity.class));
                return true;
            }
            return false;
        });

        btnMore.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, btnMore);
            menu.getMenuInflater().inflate(R.menu.dashboard_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {
                    sessionManager.logout();
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
            menu.show();
        });

        fetchUserLocation();
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQ
            );
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(loc -> {
            userLocation = loc;
            applySearch();
        });
    }

    private void applySearch() {

        List<PgModel> result = PgRepository.searchPgs(
                this,
                edtSearch.getText().toString(),
                selectedLocation,
                addressKeyword,
                minRent,
                maxRent
        );

        if (userLocation != null) {
            for (PgModel pg : result) {
                if (pg.getLatitude() == 0 || pg.getLongitude() == 0) continue;

                float[] dist = new float[1];
                Location.distanceBetween(
                        userLocation.getLatitude(),
                        userLocation.getLongitude(),
                        pg.getLatitude(),
                        pg.getLongitude(),
                        dist
                );
                pg.setDistanceKm(dist[0] / 1000f);
            }

            Collections.sort(result, Comparator.comparing(PgModel::getDistanceKm));
        }

        currentList.clear();
        currentList.addAll(result);
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void showFilterDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_filter_pg, null);

        EditText edtCity = view.findViewById(R.id.edtCity);
        EditText edtAddress = view.findViewById(R.id.edtAddress);
        EditText edtMinRent = view.findViewById(R.id.edtMinRent);
        EditText edtMaxRent = view.findViewById(R.id.edtMaxRent);
        Button btnApply = view.findViewById(R.id.btnApplyFilter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        btnApply.setOnClickListener(v -> {

            selectedLocation = edtCity.getText().toString().trim();
            addressKeyword = edtAddress.getText().toString().trim();

            minRent = edtMinRent.getText().toString().isEmpty()
                    ? null
                    : Integer.parseInt(edtMinRent.getText().toString());

            maxRent = edtMaxRent.getText().toString().isEmpty()
                    ? null
                    : Integer.parseInt(edtMaxRent.getText().toString());

            applySearch();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateEmptyState() {
        if (currentList.isEmpty()) {
            recyclerPgList.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerPgList.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        }
    }
}
