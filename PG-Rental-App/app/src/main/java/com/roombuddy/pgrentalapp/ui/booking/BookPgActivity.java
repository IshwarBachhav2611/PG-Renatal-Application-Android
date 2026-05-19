package com.roombuddy.pgrentalapp.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.BookingModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;
import com.roombuddy.pgrentalapp.utils.SessionManager;

public class BookPgActivity extends AppCompatActivity {

    private TextView tvPgName, tvRent, tvStatus;
    private Button btnConfirmBooking;

    private SessionManager sessionManager;

    private String pgName, rent, ownerEmail, pgAddress, pgCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pg);

        // ================= BIND VIEWS =================
        tvPgName = findViewById(R.id.tvPgName);
        tvRent = findViewById(R.id.tvRent);
        tvStatus = findViewById(R.id.tvStatus);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        sessionManager = new SessionManager(this);

        // ================= GET INTENT DATA =================
        pgName = getIntent().getStringExtra("pg_name");
        rent = getIntent().getStringExtra("pg_rent");
        ownerEmail = getIntent().getStringExtra("owner_email");
        pgAddress = getIntent().getStringExtra("pg_address");
        pgCity = getIntent().getStringExtra("pg_city");

        tvPgName.setText(pgName);
        tvRent.setText("₹ " + rent + " / Month");

        String studentEmail = sessionManager.getUserEmail();

        // ================= CHECK EXISTING BOOKING =================
        BookingModel existingBooking =
                BookingRepository.getBooking(this, studentEmail, pgName);

        if (existingBooking != null) {
            // Booking already exists → apply its status
            applyStatusUI(existingBooking.getStatus());
        } else {
            // No booking yet → allow booking
            tvStatus.setText("Status: NOT BOOKED");
            tvStatus.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.status_pending_bg)
            );
            tvStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.status_pending_text)
            );
            btnConfirmBooking.setEnabled(true);
        }

        // ================= CONFIRM BOOKING =================
        btnConfirmBooking.setOnClickListener(v -> {

            if (!BookingRepository.canApplyForBooking(this, studentEmail, pgName)) {
                Toast.makeText(
                        this,
                        "Booking already pending or accepted",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            BookingModel booking = new BookingModel(
                    pgName,
                    rent,
                    pgAddress,
                    pgCity,
                    sessionManager.getUserName(),
                    studentEmail,
                    sessionManager.getUserPhone(),
                    ownerEmail,
                    "PENDING"
            );

            BookingRepository.addBooking(this, booking);

            Toast.makeText(
                    this,
                    "Booking request sent successfully",
                    Toast.LENGTH_SHORT
            ).show();

            applyStatusUI("PENDING");
        });
    }

    // ================= STATUS UI =================
    private void applyStatusUI(String status) {

        tvStatus.setText("Status: " + status.toUpperCase());

        switch (status.toLowerCase()) {

            case "accepted":
                tvStatus.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.status_accepted_bg)
                );
                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.status_accepted_text)
                );
                btnConfirmBooking.setEnabled(false);
                break;

            case "rejected":
                tvStatus.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.status_rejected_bg)
                );
                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.status_rejected_text)
                );
                btnConfirmBooking.setEnabled(true); // ✅ reapply allowed
                break;

            default: // pending
                tvStatus.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.status_pending_bg)
                );
                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.status_pending_text)
                );
                btnConfirmBooking.setEnabled(false);
                break;
        }
    }
}