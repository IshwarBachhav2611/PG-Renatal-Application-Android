package com.roombuddy.pgrentalapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;

public class RoleSelectionActivity extends AppCompatActivity {

    Button btnUser, btnOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        btnUser = findViewById(R.id.btnUser);
        btnOwner = findViewById(R.id.btnOwner);

        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnOwner.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}
