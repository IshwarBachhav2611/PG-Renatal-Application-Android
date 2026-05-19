package com.roombuddy.pgrentalapp.ui.owner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.database.DatabaseHelper;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;

public class OwnerProfileActivity extends AppCompatActivity {

    private EditText edtOwnerName, edtPgName, edtEmail,
            edtMobile, edtAddress, edtCity,
            edtOldPassword, edtNewPassword;
    private Button btnUpdateProfile;

    private OwnerSessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        sessionManager = new OwnerSessionManager(this);
        dbHelper = new DatabaseHelper(this);

        edtOwnerName = findViewById(R.id.edtOwnerName);
        edtPgName = findViewById(R.id.edtPgName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        edtPgName.setEnabled(false);
        edtPgName.setFocusable(false);
        edtPgName.setClickable(false);

        loadOwnerData();

        btnUpdateProfile.setOnClickListener(v -> updateOwnerProfile());
    }

    private void loadOwnerData() {
        edtOwnerName.setText(sessionManager.getName());
        edtPgName.setText(sessionManager.getPgName()); // SHOW ONLY
        edtEmail.setText(sessionManager.getEmail());
        edtEmail.setEnabled(false);
        edtMobile.setText(sessionManager.getMobile());
        edtAddress.setText(sessionManager.getAddress());
        edtCity.setText(sessionManager.getCity());
    }

    private void updateOwnerProfile() {

        String name = edtOwnerName.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String city = edtCity.getText().toString().trim();
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Owner name required", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String currentPassword = "";
        Cursor c = db.rawQuery(
                "SELECT password FROM owner WHERE email=?",
                new String[]{sessionManager.getEmail()}
        );

        if (c.moveToFirst()) {
            currentPassword = c.getString(0);
        }
        c.close();

        String finalPassword = currentPassword;

        if (!TextUtils.isEmpty(newPass)) {
            if (!oldPass.equals(currentPassword)) {
                Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show();
                db.close();
                return;
            }
            finalPassword = newPass;
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", mobile);
        values.put("address", address);
        values.put("city", city);
        values.put("password", finalPassword);

        int rows = db.update(
                "owner",
                values,
                "email=?",
                new String[]{sessionManager.getEmail()}
        );

        db.close();

        if (rows > 0) {

            sessionManager.saveOwnerProfile(
                    name,
                    sessionManager.getPgName(),
                    sessionManager.getEmail(),
                    mobile,
                    address,
                    city
            );

            edtOldPassword.setText("");
            edtNewPassword.setText("");

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnerData();
    }
}
