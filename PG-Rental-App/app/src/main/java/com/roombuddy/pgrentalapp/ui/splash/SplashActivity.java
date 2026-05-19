package com.roombuddy.pgrentalapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.ui.home.HomeActivity;
import com.roombuddy.pgrentalapp.ui.owner.OwnerDashboardActivity;
import com.roombuddy.pgrentalapp.ui.student.StudentDashboardActivity;
import com.roombuddy.pgrentalapp.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {

            Intent intent;

            if (sessionManager.isLoggedIn()) {

                if (SessionManager.ROLE_OWNER.equals(sessionManager.getUserRole())) {
                    intent = new Intent(this, OwnerDashboardActivity.class);
                } else {
                    intent = new Intent(this, StudentDashboardActivity.class);
                }

            } else {
                intent = new Intent(this, HomeActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }, SPLASH_TIME);
    }
}
