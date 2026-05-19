package com.roombuddy.pgrentalapp.ui.owner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.adapter.OwnerBookingAdapter;
import com.roombuddy.pgrentalapp.model.BookingModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;

import java.util.Collections;
import java.util.List;

public class OwnerBookingActivity extends AppCompatActivity {

    RecyclerView recycler;
    View layoutEmpty;

    OwnerSessionManager sessionManager;
    OwnerBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_booking);

        recycler = findViewById(R.id.recyclerOwnerBookings);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = new OwnerSessionManager(this);

        loadData();
    }

    private void loadData() {

        List<BookingModel> list =
                BookingRepository.getBookingsForOwner(
                        this,
                        sessionManager.getEmail()
                );

        // 🔥 SORT: NEW REQUESTS FIRST
        Collections.sort(list, (b1, b2) ->
                Integer.compare(
                        getStatusPriority(b1.getStatus()),
                        getStatusPriority(b2.getStatus())
                )
        );

        adapter = new OwnerBookingAdapter(this, list);
        recycler.setAdapter(adapter);

        if (list.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
