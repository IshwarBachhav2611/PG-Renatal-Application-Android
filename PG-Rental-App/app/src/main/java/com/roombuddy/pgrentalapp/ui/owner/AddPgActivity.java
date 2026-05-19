package com.roombuddy.pgrentalapp.ui.owner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.database.DatabaseHelper;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;
import com.roombuddy.pgrentalapp.utils.PgRepository;

public class AddPgActivity extends AppCompatActivity {

    private EditText edtPgName, edtAddress, edtLocation,
            edtRent, edtDescription, edtContact;
    private Spinner spinnerType;
    private Button btnAddPg, btnSelectImage;
    private ImageView imgPg;

    private Uri selectedImageUri;

    private FusedLocationProviderClient locationClient;
    private static final int LOCATION_REQ = 201;

    private OwnerSessionManager ownerSessionManager;

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null &&
                                result.getData().getData() != null) {

                            selectedImageUri = result.getData().getData();

                            final int takeFlags =
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

                            getContentResolver()
                                    .takePersistableUriPermission(
                                            selectedImageUri,
                                            takeFlags
                                    );

                            imgPg.setImageURI(selectedImageUri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pg);

        // Bind views
        edtPgName = findViewById(R.id.edtPgName);
        edtAddress = findViewById(R.id.edtAddress);
        edtLocation = findViewById(R.id.edtLocation);
        edtRent = findViewById(R.id.edtRent);
        edtDescription = findViewById(R.id.edtDescription);
        edtContact = findViewById(R.id.edtContact);
        spinnerType = findViewById(R.id.spinnerType);
        btnAddPg = findViewById(R.id.btnAddPg);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgPg = findViewById(R.id.imgPg);

        new DatabaseHelper(this).getWritableDatabase();

        ownerSessionManager = new OwnerSessionManager(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!ownerSessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login as Owner", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Boys", "Girls", "Both"}
        );
        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerType.setAdapter(typeAdapter);

        btnSelectImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            imagePicker.launch(i);
        });

        btnAddPg.setOnClickListener(v -> {
            if (validatePgForm()) {
                addPgWithLocation();
            }
        });
    }

    private boolean validatePgForm() {

        String enteredPgName = edtPgName.getText().toString().trim();
        String profilePgName = ownerSessionManager.getPgName().trim();

        if (TextUtils.isEmpty(enteredPgName)) {
            edtPgName.setError("PG Name required");
            return false;
        }

        if (!TextUtils.isEmpty(profilePgName)
                && !profilePgName.equalsIgnoreCase(enteredPgName)) {

            Toast.makeText(
                    this,
                    "PG name must match Owner Profile PG name",
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }

        if (TextUtils.isEmpty(edtAddress.getText().toString().trim())) {
            edtAddress.setError("Address required");
            return false;
        }

        if (TextUtils.isEmpty(edtLocation.getText().toString().trim())) {
            edtLocation.setError("City required");
            return false;
        }

        if (TextUtils.isEmpty(edtRent.getText().toString().trim())) {
            edtRent.setError("Rent required");
            return false;
        }

        if (TextUtils.isEmpty(edtDescription.getText().toString().trim())) {
            edtDescription.setError("Description required");
            return false;
        }

        if (TextUtils.isEmpty(edtContact.getText().toString().trim())) {
            edtContact.setError("Contact required");
            return false;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select PG image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addPgWithLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQ
            );
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {

            double lat = location != null ? location.getLatitude() : 0.0;
            double lng = location != null ? location.getLongitude() : 0.0;

            PgModel pg = new PgModel(
                    edtPgName.getText().toString().trim(),
                    edtLocation.getText().toString().trim(),
                    edtRent.getText().toString().trim(),
                    spinnerType.getSelectedItem().toString(),
                    edtAddress.getText().toString().trim(),
                    edtDescription.getText().toString().trim(),
                    ownerSessionManager.getName(),
                    edtContact.getText().toString().trim(),
                    ownerSessionManager.getEmail(),
                    selectedImageUri.toString(),
                    lat,
                    lng
            );

            PgRepository.addPg(this, pg);

            Log.d("ADD_PG", "PG added successfully");

            Toast.makeText(this, "PG Added Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQ &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPgWithLocation();
        }
    }
}
