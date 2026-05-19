package com.roombuddy.pgrentalapp.ui.owner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;
import com.roombuddy.pgrentalapp.utils.PgRepository;

public class UpdatePgActivity extends AppCompatActivity {

    private ImageView imgPg;
    private Button btnChangeImage, btnUpdate;

    private EditText edtName, edtRent, edtLocation,
            edtAddress, edtContact, edtDesc;
    private Spinner spinnerType;

    private PgModel pg;
    private String imageUri;

    private OwnerSessionManager sessionManager;

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null &&
                                result.getData().getData() != null) {

                            Uri uri = result.getData().getData();
                            imageUri = uri.toString();

                            final int takeFlags =
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                            getContentResolver()
                                    .takePersistableUriPermission(uri, takeFlags);

                            imgPg.setImageURI(uri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pg);

        sessionManager = new OwnerSessionManager(this);

        imgPg = findViewById(R.id.imgPg);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnUpdate = findViewById(R.id.btnUpdatePg);

        edtName = findViewById(R.id.edtPgName);
        edtRent = findViewById(R.id.edtRent);
        edtLocation = findViewById(R.id.edtLocation);
        edtAddress = findViewById(R.id.edtAddress);
        edtContact = findViewById(R.id.edtContact);
        edtDesc = findViewById(R.id.edtDescription);
        spinnerType = findViewById(R.id.spinnerType);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Boys", "Girls", "Both"}
        );
        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerType.setAdapter(typeAdapter);

        int pgId = getIntent().getIntExtra("pg_id", -1);
        if (pgId == -1) {
            Toast.makeText(this, "Invalid PG", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pg = PgRepository.getPgById(this, pgId);
        if (pg == null) {
            Toast.makeText(this, "PG not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!pg.getOwnerEmail().equalsIgnoreCase(sessionManager.getEmail())) {
            Toast.makeText(this, "Unauthorized access", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        edtName.setText(pg.getName());
        edtRent.setText(pg.getRent());
        edtLocation.setText(pg.getLocation());
        edtAddress.setText(pg.getAddress());
        edtContact.setText(pg.getOwnerPhone());
        edtDesc.setText(pg.getDescription());

        int pos = typeAdapter.getPosition(pg.getType());
        if (pos >= 0) spinnerType.setSelection(pos);

        imageUri = pg.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            imgPg.setImageURI(Uri.parse(imageUri));
        }

        btnChangeImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            imagePicker.launch(i);
        });


        btnUpdate.setOnClickListener(v -> updatePg());
    }

    private void updatePg() {

        String pgName = edtName.getText().toString().trim();
        String rent = edtRent.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String contact = edtContact.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (pgName.isEmpty() || rent.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        pg.setName(pgName);
        pg.setRent(rent);
        pg.setLocation(location);
        pg.setAddress(address);
        pg.setType(type);
        pg.setDescription(desc);
        pg.setOwnerPhone(contact);
        pg.setImageUri(imageUri);

        PgRepository.updatePg(this, pg);

        sessionManager.saveOwnerProfile(
                sessionManager.getName(),
                pgName,
                sessionManager.getEmail(),
                contact,
                sessionManager.getAddress(),
                sessionManager.getCity()
        );

        Toast.makeText(this, "PG updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
