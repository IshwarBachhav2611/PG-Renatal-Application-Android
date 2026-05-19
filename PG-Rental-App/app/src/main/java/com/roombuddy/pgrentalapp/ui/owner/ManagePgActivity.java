package com.roombuddy.pgrentalapp.ui.owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;
import com.roombuddy.pgrentalapp.utils.OwnerSessionManager;
import com.roombuddy.pgrentalapp.utils.PgRepository;

import java.util.List;

public class ManagePgActivity extends AppCompatActivity {

    private Button btnAddPg, btnUpdatePg, btnDeletePg;
    private OwnerSessionManager sessionManager;
    private List<PgModel> ownerPgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pg);

        btnAddPg = findViewById(R.id.btnAddPg);
        btnUpdatePg = findViewById(R.id.btnUpdatePg);
        btnDeletePg = findViewById(R.id.btnDeletePg);

        sessionManager = new OwnerSessionManager(this);

        loadOwnerPgs();

        btnAddPg.setOnClickListener(v -> {
            if (!ownerPgs.isEmpty()) {
                Toast.makeText(
                        this,
                        "You can add only one PG per account",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            startActivity(new Intent(this, AddPgActivity.class));
        });

        btnUpdatePg.setOnClickListener(v -> {
            if (ownerPgs.isEmpty()) {
                Toast.makeText(
                        this,
                        "No PG available to update",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            PgModel pg = ownerPgs.get(0);

            Intent intent = new Intent(this, UpdatePgActivity.class);
            intent.putExtra("pg_id", pg.getPgId());
            startActivity(intent);
        });


        btnDeletePg.setOnClickListener(v -> {
            if (ownerPgs.isEmpty()) {
                Toast.makeText(
                        this,
                        "No PG available to delete",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            PgModel pg = ownerPgs.get(0);

            new AlertDialog.Builder(this)
                    .setTitle("Delete PG")
                    .setMessage(
                            "Deleting this PG will also remove all booking requests.\n\nDo you want to continue?"
                    )
                    .setPositiveButton("Yes", (d, w) -> {

                        BookingRepository.deleteBookingsByPg(
                                this,
                                pg.getName()
                        );

                        PgRepository.deletePg(this, pg.getPgId());

                        Toast.makeText(
                                this,
                                "PG and related bookings deleted successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        ownerPgs.clear();

                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadOwnerPgs() {
        ownerPgs = PgRepository.getPgsByOwner(
                this,
                sessionManager.getEmail()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnerPgs();
    }
}
