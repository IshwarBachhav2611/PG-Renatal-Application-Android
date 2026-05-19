package com.roombuddy.pgrentalapp.ui.booking;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.utils.SessionManager;

public class BookingStatusActivity extends AppCompatActivity {

    TextView txtPgName, txtLocation, txtStatus;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        txtPgName = findViewById(R.id.txtPgName);
        txtLocation = findViewById(R.id.txtLocation);
        txtStatus = findViewById(R.id.txtStatus);

        sessionManager = new SessionManager(this);

        txtPgName.setText("Shree PG");
        txtLocation.setText("Pune");

        String status = sessionManager.getBookingStatus(1);
        txtStatus.setText("Status: " + status);
    }
}
