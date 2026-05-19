package com.roombuddy.pgrentalapp.ui.pg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.database.dao.OwnerDao;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.ui.booking.BookPgActivity;
import com.roombuddy.pgrentalapp.ui.map.NearestPgMapActivity;
import com.roombuddy.pgrentalapp.utils.PgRepository;
import com.roombuddy.pgrentalapp.utils.RatingRepository;

public class PgDetailActivity extends AppCompatActivity {

    private ImageView imgPg;
    private TextView tvPgName, tvRent, tvType,
            tvAddress, tvPgContact, tvDescription, txtRatingCount;
    private RatingBar ratingBarOverall;
    private Button btnBookPg;
    private ImageButton btnContactOwner, btnRatePg, btnViewReviews, btnViewOnMap;

    private PgModel pg;
    private String ownerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_detail);

        imgPg = findViewById(R.id.imgPg);
        tvPgName = findViewById(R.id.tvPgName);
        tvRent = findViewById(R.id.tvRent);
        tvType = findViewById(R.id.tvType);
        tvAddress = findViewById(R.id.tvAddress);
        tvPgContact = findViewById(R.id.tvPgContact);
        tvDescription = findViewById(R.id.tvDescription);
        ratingBarOverall = findViewById(R.id.ratingBarOverall);
        txtRatingCount = findViewById(R.id.txtRatingCount);

        btnBookPg = findViewById(R.id.btnBookPg);
        btnContactOwner = findViewById(R.id.btnContactOwner);
        btnRatePg = findViewById(R.id.btnRatePg);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);

        int pgId = getIntent().getIntExtra("pg_id", -1);

        if (pgId == -1) {
            Toast.makeText(this, "PG detail not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pg = PgRepository.getPgById(this, pgId);

        if (pg == null) {
            Toast.makeText(this, "PG details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        OwnerDao ownerDao = new OwnerDao(this);
        ownerPhone = ownerDao.getOwnerPhoneByEmail(pg.getOwnerEmail());

        tvPgName.setText(pg.getName());
        tvRent.setText("₹ " + pg.getRent() + " / Month");
        tvType.setText("Type: " + pg.getType());
        tvAddress.setText("Address: " + pg.getAddress() + ", " + pg.getLocation());
        tvDescription.setText(pg.getDescription());
        tvPgContact.setText("PG Contact: " + pg.getOwnerPhone());

        if (pg.getImageUri() != null && !pg.getImageUri().isEmpty()) {
            imgPg.setImageURI(Uri.parse(pg.getImageUri()));
        } else {
            imgPg.setImageResource(R.drawable.ic_launcher_background);
        }

        float avgRating = RatingRepository.getAverageRating(this, pg.getName());
        int ratingCount = RatingRepository.getRatingCount(this, pg.getName());
        ratingBarOverall.setRating(avgRating);
        txtRatingCount.setText("(" + ratingCount + " reviews)");

        btnBookPg.setOnClickListener(v -> {
            Intent bookIntent = new Intent(this, BookPgActivity.class);
            bookIntent.putExtra("pg_name", pg.getName());
            bookIntent.putExtra("pg_rent", pg.getRent());
            bookIntent.putExtra("owner_email", pg.getOwnerEmail());
            bookIntent.putExtra("pg_address", pg.getAddress());
            bookIntent.putExtra("pg_city", pg.getLocation());
            startActivity(bookIntent);
        });


        btnContactOwner.setOnClickListener(v -> showContactDialog());

        btnRatePg.setOnClickListener(v -> {
            Intent rateIntent = new Intent(this, RatePgActivity.class);
            rateIntent.putExtra("pg_name", pg.getName());
            startActivity(rateIntent);
        });


        btnViewReviews.setOnClickListener(v -> {
            Intent reviewIntent = new Intent(this, ReviewListActivity.class);
            reviewIntent.putExtra("pg_name", pg.getName());
            startActivity(reviewIntent);
        });

        btnViewOnMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, NearestPgMapActivity.class);
            mapIntent.putExtra("from_pg_detail", true);
            mapIntent.putExtra("pg_id", pg.getPgId());
            mapIntent.putExtra("pg_lat", pg.getLatitude());
            mapIntent.putExtra("pg_lng", pg.getLongitude());
            startActivity(mapIntent);
        });
    }

    private void showContactDialog() {

        if (ownerPhone == null || ownerPhone.trim().isEmpty()) {
            Toast.makeText(this, "Owner contact not available", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Contact Owner")
                .setMessage(
                        "Owner Name: " + pg.getOwnerName() + "\n\n" +
                                "Mobile No: " + ownerPhone + "\n\n" +
                                "Email: " + pg.getOwnerEmail()
                )
                .setPositiveButton("Call Now", (d, w) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + ownerPhone));
                    startActivity(callIntent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
