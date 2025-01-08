package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class AddPengumuman extends AppCompatActivity {

    private EditText nama_pengumuman, isi_pengumuman;
    private String strNamaPengumuman, strIsiPengumuman, strTingkatan, strRegional, strChapter;
    private Spinner tingkatan, regional, chapter;
    private MaterialToolbar btnKembali;
    private FirebaseFirestore db;
    private Button btnSubmit;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pengumuman);

        db = FirebaseFirestore.getInstance();

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nama_pengumuman = findViewById(R.id.nama_pengumuman);
        isi_pengumuman = findViewById(R.id.isi_pengumuman);

        tingkatan = findViewById(R.id.dropdown_tingkatan);
        ArrayAdapter<CharSequence> adapterTingkatanPengumuman = ArrayAdapter.createFromResource(
                this,
                R.array.add_pengumuman_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanPengumuman.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tingkatan.setAdapter(adapterTingkatanPengumuman);
        tingkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regional = findViewById(R.id.dropdown_regional);
        ArrayAdapter<CharSequence> adapterRegionalPengumuman = ArrayAdapter.createFromResource(
                this,
                R.array.pengumuman_regional_array,
                R.layout.custom_spinner_item
        );
        adapterRegionalPengumuman.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regional.setAdapter(adapterRegionalPengumuman);
        regional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chapter = findViewById(R.id.dropdown_chapter);
        ArrayAdapter<CharSequence> adapterChapterPengumuman = ArrayAdapter.createFromResource(
                this,
                R.array.pengumuman_chapter_array,
                R.layout.custom_spinner_item
        );
        adapterChapterPengumuman.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapter.setAdapter(adapterChapterPengumuman);
        chapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit = findViewById(R.id.button_simpan);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });
    }

    private void submitRequest() {
        strNamaPengumuman = nama_pengumuman.getText().toString();
        strIsiPengumuman = isi_pengumuman.getText().toString();
        strTingkatan = tingkatan.getSelectedItem().toString();
        strRegional = regional.getSelectedItem().toString();
        strChapter = chapter.getSelectedItem().toString();

        if (strNamaPengumuman.isEmpty() || strIsiPengumuman.isEmpty() || strTingkatan.isEmpty() || strRegional.isEmpty() || strChapter.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dataPengumuman = new HashMap<>();
        dataPengumuman.put("judulPengumuman", strNamaPengumuman);
        dataPengumuman.put("isiPengumuman", strIsiPengumuman);
        dataPengumuman.put("namaTingkatan", strTingkatan);
        dataPengumuman.put("namaRegional", strRegional);
        dataPengumuman.put("namaChapter", strChapter);

        db.collection("pengumuman")
                .add(dataPengumuman)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showConfirmationDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPengumuman.this, "Gagal membuat Pengumuman" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Pengumuman berhasil dibuat!")
                .setPositiveButton("Oke.", (dialog, which) -> {
                    Intent intent = new Intent(this, PengumumanActivity.class);
                    startActivity(intent);
                })
                .show();
    }
}