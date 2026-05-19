package com.roombuddy.pgrentalapp.ui.student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etMobile;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
