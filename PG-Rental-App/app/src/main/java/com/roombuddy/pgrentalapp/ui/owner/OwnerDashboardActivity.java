package com.roombuddy.pgrentalapp.ui.owner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.ui.home.HomeActivity;
import com.roombuddy.pgrentalapp.ui.pg.ReviewListActivity;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;
import com.roombuddy.pgrentalapp.utils.PgRepository;
import com.roombuddy.pgrentalapp.utils.RatingRepository;

import java.util.List;

public class OwnerDashboardActivity extends AppCompatActivity {

    // Header
    private TextView txtGreeting;
    private ImageButton btnMore;

    // PG Card
    private CardView cardPg;
    private ImageView imgPg;
    private TextView tvPgName, tvAddress, tvRent, txtReviewCount;
    private RatingBar ratingBar;
    private Button btnViewReviews;

    // Empty state
    private LinearLayout layoutNoPg;

    // Bottom
    private Button btnManagePg;
    private BottomNavigationView bottomNavigation;

    private OwnerSessionManager ownerSessionManager;

    private List<PgModel> ownerPgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        ownerSessionManager = new OwnerSessionManager(this);

        txtGreeting = findViewById(R.id.txtGreeting);
        btnMore = findViewById(R.id.btnMore);

        cardPg = findViewById(R.id.cardPg);
        imgPg = findViewById(R.id.imgPg);
        tvPgName = findViewById(R.id.tvPgName);
        tvAddress = findViewById(R.id.tvAddress);
        tvRent = findViewById(R.id.tvRent);
        ratingBar = findViewById(R.id.ratingBar);
        txtReviewCount = findViewById(R.id.txtReviewCount);
        btnViewReviews = findViewById(R.id.btnViewReviews);

        layoutNoPg = findViewById(R.id.layoutNoPg);
        btnManagePg = findViewById(R.id.btnManagePg);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        loadOwnerPgs();
        setupGreeting();
        setupPgCard();
        setupManagePgButton();
        setupMenu();
        setupBottomNavigation();
    }

    private void loadOwnerPgs() {
        String ownerEmail = ownerSessionManager.getEmail();
        ownerPgs = PgRepository.getPgsByOwner(this, ownerEmail);

    }

    private void setupGreeting() {
        String ownerName = ownerSessionManager.getName();

        if (ownerPgs.isEmpty()) {
            txtGreeting.setText("Hello " + ownerName + " 👋");
        } else {
            txtGreeting.setText(
                    "Hello " + ownerName + " 👋\nYour PG: " + ownerPgs.get(0).getName()
            );
        }
    }

    private void setupPgCard() {

        if (ownerPgs.isEmpty()) {
            layoutNoPg.setVisibility(View.VISIBLE);
            cardPg.setVisibility(View.GONE);
            return;
        }

        layoutNoPg.setVisibility(View.GONE);
        cardPg.setVisibility(View.VISIBLE);

        PgModel pg = ownerPgs.get(0);

        tvPgName.setText(pg.getName());
        tvAddress.setText(pg.getAddress());
        tvRent.setText("₹ " + pg.getRent() + " / Month");

        float avgRating = RatingRepository.getAverageRating(this, pg.getName());
        int count = RatingRepository.getRatingCount(this, pg.getName());

        ratingBar.setRating(avgRating);
        txtReviewCount.setText("(" + count + " reviews)");

        if (pg.getImageUri() != null && !pg.getImageUri().isEmpty()) {
            imgPg.setImageURI(Uri.parse(pg.getImageUri()));
        } else {
            imgPg.setImageResource(R.drawable.ic_launcher_background);
        }

        btnViewReviews.setOnClickListener(v -> {
            Intent i = new Intent(this, ReviewListActivity.class);
            i.putExtra("pg_name", pg.getName());
            startActivity(i);
        });
    }

    private void setupManagePgButton() {

        if (ownerPgs.isEmpty()) {
            btnManagePg.setText("Add PG");
            btnManagePg.setOnClickListener(v ->
                    startActivity(new Intent(this, AddPgActivity.class))
            );
        } else {
            btnManagePg.setText("Manage PG");
            btnManagePg.setOnClickListener(v ->
                    startActivity(new Intent(this, ManagePgActivity.class))
            );
        }
    }

    private void setupMenu() {
        btnMore.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, btnMore);
            menu.getMenuInflater().inflate(R.menu.dashboard_menu, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_logout) {

                    ownerSessionManager.logout();

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
    }

    private void setupBottomNavigation() {

        bottomNavigation.setSelectedItemId(R.id.nav_manage_pg);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_manage_pg) return true;

            if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, OwnerBookingActivity.class));
                return true;
            }

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, OwnerProfileActivity.class));
                return true;
            }

            if (id == R.id.nav_all_pgs) {
                startActivity(new Intent(this, OwnerAllPgsActivity.class));
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnerPgs();
        setupGreeting();
        setupPgCard();
        setupManagePgButton();
    }
}
