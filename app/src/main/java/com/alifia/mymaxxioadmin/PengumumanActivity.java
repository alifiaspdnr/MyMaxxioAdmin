package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alifia.mymaxxioadmin.model.Pengumuman;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PengumumanActivity extends AppCompatActivity {

    private RecyclerView pengumumanList;
    private PengumumanAdapter adapter;
    //private List<Pengumuman> pengumumanDataList;
    private FirebaseFirestore db;
    private Spinner btnTingkatanPengumuman;
    private ImageButton btnAddPengumuman;
    private MaterialToolbar btnKembali;
    private CollectionReference pengumumanRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pengumuman);

        db = FirebaseFirestore.getInstance();
        pengumumanRef = db.collection("pengumuman");

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // untuk menampilkan list pengumuman, digunakan recyclerview
        pengumumanList = findViewById(R.id.pengumumanList);
        pengumumanList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PengumumanAdapter(new ArrayList<>());
        pengumumanList.setAdapter(adapter);

        btnAddPengumuman = findViewById(R.id.button_add_pengumuman);
        btnAddPengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPengumuman.class);
                startActivity(intent);
            }
        });

        btnTingkatanPengumuman = findViewById(R.id.dropdown_pengumuman);
        ArrayAdapter<CharSequence> adapterTingkatanPengumuman = ArrayAdapter.createFromResource(
                this,
                R.array.pengumuman_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanPengumuman.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btnTingkatanPengumuman.setAdapter(adapterTingkatanPengumuman);
        btnTingkatanPengumuman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTingkatan = btnTingkatanPengumuman.getSelectedItem().toString();
//                if (selectedTingkatan.equals("Semua Pengumuman")) {
//                    fetchAllPengumuman();
//                } else {
//                    fetchPengumumanByTingkatan(selectedTingkatan);
//                }
                setupFirestore(selectedTingkatan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setupFirestore("Semua Pengumuman");
    }

    private void setupFirestore(String level) {
        if (level.equals("Semua Pengumuman")) {
            pengumumanRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> pengumumanList = task.getResult().getDocuments();
                        adapter.setPengumumanList(pengumumanList);
                    }
                }
            });
        } else {
            pengumumanRef.whereEqualTo("namaTingkatan", level).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> pengumumanList = task.getResult().getDocuments();
                        adapter.setPengumumanList(pengumumanList);
                    }
                }
            });
        }
    }
}