package com.alifia.mymaxxioadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailMemberDitolak extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    private ImageView fotoKta;
    private TextView idAnggota, nama, email, noHp, regional, chapter, nopol, alasan;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_member_ditolak);

        db = FirebaseFirestore.getInstance();

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fotoKta = findViewById(R.id.foto_kta);
        idAnggota = findViewById(R.id.id_anggota);
        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        noHp = findViewById(R.id.no_hp);
        regional = findViewById(R.id.regional);
        chapter = findViewById(R.id.chapter);
        nopol = findViewById(R.id.nopol);
        alasan = findViewById(R.id.alasan);

        // mengeluarkan data intent extra dari MemberSemuaAdapter
        String namaMember = getIntent().getStringExtra("namaMember");
        Log.d("namaMember nyaaa : ", namaMember);
        if (namaMember != null) {
            setupFirestore(namaMember);
        }
    }

    private void setupFirestore(String namaMember) {
        db.collection("users_non_registered")
                .whereEqualTo("namaLengkap", namaMember)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            DocumentSnapshot document = documents.get(0);
                            idAnggota.setText(document.getString("idAnggota"));
                            nama.setText(document.getString("namaLengkap"));
                            email.setText(document.getString("email"));
                            noHp.setText(document.getString("noHp"));
                            regional.setText(document.getString("namaRegional"));
                            chapter.setText(document.getString("namaChapter"));
                            nopol.setText(document.getString("nopol"));
                            alasan.setText(document.getString("alasan"));

                            String imageUrl = document.getString("fotoKTA");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                fotoKta.setVisibility(View.VISIBLE);
                                Glide.with(DetailMemberDitolak.this)
                                        .load(imageUrl)
                                        .into(fotoKta);
                            }
                        } else {
                            Toast.makeText(DetailMemberDitolak.this, "Data not found for: " + namaMember, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the error
                        Toast.makeText(DetailMemberDitolak.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}