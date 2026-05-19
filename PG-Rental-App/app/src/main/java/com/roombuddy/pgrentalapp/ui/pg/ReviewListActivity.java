package com.roombuddy.pgrentalapp.ui.pg;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.adapter.ReviewAdapter;
import com.roombuddy.pgrentalapp.model.ReviewModel;
import com.roombuddy.pgrentalapp.utils.ReviewRepository;

import java.util.List;

public class ReviewListActivity extends AppCompatActivity {

    private RecyclerView recyclerReviews;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        recyclerReviews = findViewById(R.id.recyclerReviews);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));

        String pgName = getIntent().getStringExtra("pg_name");

        if (pgName == null) {
            Toast.makeText(this, "PG not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<ReviewModel> reviews =
                ReviewRepository.getReviews(this, pgName);

        if (reviews.isEmpty()) {
            recyclerReviews.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerReviews.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            recyclerReviews.setAdapter(new ReviewAdapter(reviews));
        }
    }
}
