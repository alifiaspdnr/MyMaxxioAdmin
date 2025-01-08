package com.alifia.mymaxxioadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DaftarUangKasMember extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference kasMemberRef;
    private MaterialToolbar btnKembali;
    private RecyclerView recyclerView;
    private KasMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_uang_kas_member);

        db = FirebaseFirestore.getInstance();
        kasMemberRef = db.collection("kas_saya");

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KasMemberAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // mengeluarkan data intent extra dari PencarianMemberAdapter
        String namaMember = getIntent().getStringExtra("namaMember");
        if (namaMember != null) {
            setupFirestore(namaMember);
        }
    }

    private void setupFirestore(String namaMember) {
        kasMemberRef.whereEqualTo("namaMember", namaMember)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        adapter.setKasMemberList(documents);
                    } else {
                        // Handle the error
                        Toast.makeText(DaftarUangKasMember.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}