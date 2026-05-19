package com.roombuddy.pgrentalapp.ui.pg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.ReviewModel;
import com.roombuddy.pgrentalapp.utils.RatingRepository;
import com.roombuddy.pgrentalapp.utils.ReviewRepository;
import com.roombuddy.pgrentalapp.utils.SessionManager;

public class RatePgActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText edtReview;
    private Button btnSubmit;

    private String pgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_pg);

        ratingBar = findViewById(R.id.ratingBar);
        edtReview = findViewById(R.id.edtReview);
        btnSubmit = findViewById(R.id.btnSubmitRating);

        pgName = getIntent().getStringExtra("pg_name");

        if (pgName == null || pgName.isEmpty()) {
            Toast.makeText(this, "PG not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> {

            float rating = ratingBar.getRating();
            String comment = edtReview.getText().toString().trim();

            if (rating == 0f) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            if (comment.isEmpty()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager sessionManager = new SessionManager(this);
            String userName = sessionManager.getUserName();

            ReviewRepository.addReview(
                    this,
                    pgName,
                    new ReviewModel(userName, rating, comment)
            );

            RatingRepository.addRating(this, pgName, rating);

            Toast.makeText(this, "Thank you for your review!", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
