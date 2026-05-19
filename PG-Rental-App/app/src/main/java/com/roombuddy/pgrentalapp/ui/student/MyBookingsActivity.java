package com.roombuddy.pgrentalapp.ui.student;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.adapter.BookingAdapter;
import com.roombuddy.pgrentalapp.model.BookingModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;
import com.roombuddy.pgrentalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private SessionManager sessionManager;
    private TextView tvNoBookings;

    private final List<BookingModel> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        recyclerView = findViewById(R.id.recyclerBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new SessionManager(this);

        bookingAdapter = new BookingAdapter(this, bookingList);
        recyclerView.setAdapter(bookingAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bookingList.clear();
        bookingList.addAll(
                BookingRepository.getBookingsByStudent(
                        this,
                        sessionManager.getUserEmail()
                )
        );

        Collections.sort(bookingList, (b1, b2) ->
                Integer.compare(
                        getStatusPriority(b1.getStatus()),
                        getStatusPriority(b2.getStatus())
                )
        );

        bookingAdapter.notifyDataSetChanged();

        boolean isEmpty = bookingList.isEmpty();
        tvNoBookings.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private int getStatusPriority(String status) {

        if (status == null) return 4;

        switch (status.toLowerCase()) {
            case "pending":
                return 1;
            case "accepted":
                return 2;
            case "rejected":
                return 3;
            default:
                return 4;
        }
    }
}
