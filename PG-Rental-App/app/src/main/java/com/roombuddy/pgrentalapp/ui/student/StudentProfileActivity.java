package com.roombuddy.pgrentalapp.ui.student;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.database.dao.StudentDao;
import com.roombuddy.pgrentalapp.utils.SessionManager;

public class StudentProfileActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPhone, edtAddress, edtCity;
    EditText edtOldPassword, edtNewPassword;
    Spinner spinnerGender;
    Button btnUpdateProfile;

    SessionManager sessionManager;
    StudentDao studentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        sessionManager = new SessionManager(this);
        studentDao = new StudentDao(this);

        edtName.setText(sessionManager.getUserName());
        edtEmail.setText(sessionManager.getUserEmail());
        edtPhone.setText(sessionManager.getUserPhone());
        edtAddress.setText(sessionManager.getUserAddress());
        edtCity.setText(sessionManager.getUserCity());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        String savedGender = sessionManager.getUserGender();
        if (savedGender != null) {
            int position = adapter.getPosition(savedGender);
            if (position >= 0) spinnerGender.setSelection(position);
        }

        btnUpdateProfile.setOnClickListener(v -> {

            int studentId = sessionManager.getUserId();

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String city = edtCity.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();

            String oldPassword = edtOldPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(
                        this,
                        "Name, Email and Phone are required",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            boolean profileUpdated = studentDao.updateStudentProfile(
                    studentId,
                    name,
                    email,
                    phone,
                    address,
                    city,
                    gender
            );

            if (profileUpdated) {

                sessionManager.updateProfile(
                        name,
                        email,
                        phone,
                        address,
                        city,
                        gender
                );
            } else {
                Toast.makeText(this,
                        "Profile update failed",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!oldPassword.isEmpty() || !newPassword.isEmpty()) {

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(this,
                            "Please enter both old and new password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean correctOld = studentDao.checkOldPassword(
                        studentId,
                        oldPassword
                );

                if (!correctOld) {
                    Toast.makeText(this,
                            "Old password is incorrect",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean passwordUpdated = studentDao.updatePassword(
                        studentId,
                        newPassword
                );

                if (!passwordUpdated) {
                    Toast.makeText(this,
                            "Failed to update password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            edtOldPassword.setText("");
            edtNewPassword.setText("");

            Toast.makeText(
                    this,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
