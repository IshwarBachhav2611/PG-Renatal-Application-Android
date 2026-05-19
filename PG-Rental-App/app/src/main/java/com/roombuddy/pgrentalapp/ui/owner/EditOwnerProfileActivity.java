package com.roombuddy.pgrentalapp.ui.owner;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;

public class EditOwnerProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPgName, edtEmail, edtMobile, edtAddress, edtCity;
    private Button btnSaveProfile;

    // Session
    private OwnerSessionManager ownerSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_owner_profile);

        edtName = findViewById(R.id.edtName);
        edtPgName = findViewById(R.id.edtPgName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        ownerSessionManager = new OwnerSessionManager(this);

        edtName.setText(ownerSessionManager.getName());
        edtPgName.setText(ownerSessionManager.getPgName());
        edtEmail.setText(ownerSessionManager.getEmail());
        edtMobile.setText(ownerSessionManager.getMobile());
        edtAddress.setText(ownerSessionManager.getAddress());
        edtCity.setText(ownerSessionManager.getCity());

        btnSaveProfile.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String pgName = edtPgName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String mobile = edtMobile.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String city = edtCity.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                edtName.setError("Name is required");
                edtName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Email is required");
                edtEmail.requestFocus();
                return;
            }

            ownerSessionManager.saveOwnerProfile(
                    name,
                    pgName,
                    email,
                    mobile,
                    address,
                    city
            );

            Toast.makeText(
                    this,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }
}
