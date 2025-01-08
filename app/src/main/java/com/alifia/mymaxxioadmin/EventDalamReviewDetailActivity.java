package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alifia.mymaxxioadmin.model.Event;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventDalamReviewDetailActivity extends AppCompatActivity {

    private TextView eventTitle, eventDescription, description;
    private ImageView eventImage;
    private MaterialToolbar eventToolbar;
    Button btnTerima, btnTolak;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_dalam_review_detail);

        db = FirebaseFirestore.getInstance();

        eventTitle = findViewById(R.id.tvEventTitle);
        eventDescription = findViewById(R.id.tvEventDescription);
        description = findViewById(R.id.tvDescription);
        eventImage = findViewById(R.id.eventImage);

        eventToolbar = findViewById(R.id.eventToolbar);
        eventToolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String bundleEventID = bundle.getString("eventId");
        Log.d("TAG", bundleEventID);

        if (bundleEventID != null) {
            FirebaseFirestore store = FirebaseFirestore.getInstance();

            store.collection("kegiatan")
                    .document(bundleEventID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            Event event = documentSnapshot.toObject(Event.class);
                            eventTitle.setText(event.getNamaKegiatan());
                            eventDescription.setText(event.getDeskripsiSingkat());
                            description.setText(event.getDeskripsi());
                            if (Objects.equals(event.getImageUrl(), "")) {
                                eventImage.setImageResource(R.drawable.img_placeholder);
                            } else {
                                Glide.with(this)
                                        .load(event.getImageUrl())
                                        .into(eventImage);
                            }
                        }
                    });
        }

        btnTerima = findViewById(R.id.button_terima);
        btnTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatusKegiatan(1, null, bundleEventID);
            }
        });

        btnTolak = findViewById(R.id.button_tolak);
        btnTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRejectionDialog(bundleEventID);
            }
        });
    }

    private void showRejectionDialog(String bundleEventID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tolak Kegiatan Usulan");

        final EditText input = new EditText(this);
        input.setHint("Masukkan alasan penolakan");

        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(EventDalamReviewDetailActivity.this, "Alasan penolakan harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                updateStatusKegiatan(2, reason, bundleEventID);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateStatusKegiatan(int isApproved, String reason, String bundleEventID) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isApproved", isApproved);
        if (reason != null) {
            updateData.put("alasan", reason);
        }

        db.collection("kegiatan").document(bundleEventID).update(updateData)
                .addOnCompleteListener(task -> {
                    //progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(EventDalamReviewDetailActivity.this, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EventDalamReviewDetailActivity.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}