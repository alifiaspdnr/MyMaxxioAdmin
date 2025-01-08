package com.alifia.mymaxxioadmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailUangKasDitinjau extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    private ImageView fotoBuktiTransfer;
    private FirebaseFirestore db;
    Button btnTerima, btnTolak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_uang_kas_ditinjau);

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fotoBuktiTransfer = findViewById(R.id.foto_bukti_tf);

        db = FirebaseFirestore.getInstance();

        String idKas = getIntent().getStringExtra("idKas");
        if (idKas != null) {
            setupFirestore(idKas);
        }

        btnTerima = findViewById(R.id.button_terima);
        btnTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatusKas("Lunas", null, idKas);
            }
        });

        btnTolak = findViewById(R.id.button_tolak);
        btnTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRejectionDialog(idKas);
            }
        });
    }

    private void setupFirestore(String idKas) {
        db.collection("kas_saya").document(idKas).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("attachment");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                fotoBuktiTransfer.setVisibility(View.VISIBLE);
                                Glide.with(DetailUangKasDitinjau.this)
                                        .load(imageUrl)
                                        .into(fotoBuktiTransfer);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle any errors
                    }
                });
    }

    private void updateStatusKas(String status, String reason, String idKas) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", status);
        if (reason != null) {
            updateData.put("alasan", reason);
        }

        db.collection("kas_saya").document(idKas).update(updateData)
                .addOnCompleteListener(task -> {
                    //progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailUangKasDitinjau.this, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DetailUangKasDitinjau.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRejectionDialog(String idKas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tolak Bukti Transfer");

        final EditText input = new EditText(this);
        input.setHint("Masukkan alasan penolakan");

        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(DetailUangKasDitinjau.this, "Alasan penolakan harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                updateStatusKas("Ditolak", reason, idKas);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}