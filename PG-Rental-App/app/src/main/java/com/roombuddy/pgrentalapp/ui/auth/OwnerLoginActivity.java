package com.roombuddy.pgrentalapp.ui.auth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.database.dao.OwnerDao;
import com.roombuddy.pgrentalapp.ui.owner.OwnerDashboardActivity;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;

public class OwnerLoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView txtRegister;

    OwnerDao ownerDao;
    OwnerSessionManager ownerSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        ownerDao = new OwnerDao(this);
        ownerSessionManager = new OwnerSessionManager(this);

        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                        this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Cursor cursor = ownerDao.loginOwner(email, password);

            if (cursor != null && cursor.moveToFirst()) {

                int ownerId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("owner_id"));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                String pgName = cursor.getString(
                        cursor.getColumnIndexOrThrow("pg_name"));

                String phone = cursor.getString(
                        cursor.getColumnIndexOrThrow("phone"));

                String address = cursor.getString(
                        cursor.getColumnIndexOrThrow("address"));

                String city = cursor.getString(
                        cursor.getColumnIndexOrThrow("city"));

                String passwordFromDb = cursor.getString(
                        cursor.getColumnIndexOrThrow("password"));

                cursor.close();

                ownerSessionManager.login(
                        ownerId,
                        name,
                        email,
                        phone,
                        passwordFromDb
                );

                ownerSessionManager.saveOwnerProfile(
                        name,
                        pgName,
                        email,
                        phone,
                        address,
                        city
                );

                Toast.makeText(
                        this,
                        "Owner Login Successful",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        OwnerLoginActivity.this,
                        OwnerDashboardActivity.class
                );
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );
                startActivity(intent);
                finish();

            } else {
                if (cursor != null) cursor.close();
                Toast.makeText(
                        this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        txtRegister.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                OwnerLoginActivity.this,
                                OwnerRegisterActivity.class
                        )
                )
        );
    }
}
