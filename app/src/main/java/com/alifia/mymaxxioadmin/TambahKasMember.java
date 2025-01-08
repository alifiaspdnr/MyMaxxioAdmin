package com.alifia.mymaxxioadmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahKasMember extends AppCompatActivity {
    private MaterialToolbar btnKembali;
    AutoCompleteTextView namaMember;
    private EditText nominalKas;
    //private EditText namaMember;
    private Spinner bulanSpinner, tahunSpinner;
    private Button submitButton;
    private FirebaseFirestore db;
    private List<String> memberSuggestions;
    private String selectedBulan;
    private String selectedTahun;

    //@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tambah_kas_member);

        db = FirebaseFirestore.getInstance();

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bulanSpinner = findViewById(R.id.dropdown_bulan);
        ArrayAdapter<CharSequence> bulanSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.bulan_array,
                R.layout.custom_spinner_item
        );
        bulanSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bulanSpinner.setAdapter(bulanSpinnerAdapter);
        bulanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBulan = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tahunSpinner = findViewById(R.id.dropdown_tahun);
        ArrayAdapter<CharSequence> tahunSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tahun_array,
                R.layout.custom_spinner_item
        );
        tahunSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tahunSpinner.setAdapter(tahunSpinnerAdapter);
        tahunSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTahun = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nominalKas = findViewById(R.id.nominal);

        namaMember = findViewById(R.id.nama_member);
        namaMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getMemberSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submitButton = findViewById(R.id.button_simpan);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

    }

    private void getMemberSuggestions(String string) {
        CollectionReference memberRef = db.collection("member");
        memberRef.whereGreaterThanOrEqualTo("namaLengkap", string)
                .whereLessThanOrEqualTo("namaLengkap", string + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        memberSuggestions = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("namaLengkap");
                            memberSuggestions.add(name);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(TambahKasMember.this,
                                android.R.layout.simple_dropdown_item_1line, memberSuggestions);
                        namaMember.setAdapter(adapter);
                        namaMember.showDropDown();
                    }
                });
    }

    private void uploadData() {
        String nama = namaMember.getText().toString().trim();
        String nominal = nominalKas.getText().toString().trim();
        int bulanUrutan = getBulanUrutan(selectedBulan);

        Map<String, Object> kasData = new HashMap<>();
        kasData.put("namaMember", nama);
        kasData.put("bulan", selectedBulan);
        kasData.put("tahun", selectedTahun);
        kasData.put("jumlah_uang_kas", Integer.parseInt(nominal));
        kasData.put("attachment", "");
        kasData.put("bulan_urutan", bulanUrutan);
        kasData.put("status", "Belum Lunas");

        db.collection("kas_saya").
                add(kasData).
                addOnSuccessListener(documentReference -> {
            String id = documentReference.getId();
            db.collection("kas_saya").
                    document(id).
                    update("id", id);
            finish();
        }).addOnFailureListener(e -> {
            // Tangani error di sini
        });
    }

    private int getBulanUrutan(String bulan) {
        switch (bulan) {
            case "Januari":
                return 1;
            case "Februari":
                return 2;
            case "Maret":
                return 3;
            case "April":
                return 4;
            case "Mei":
                return 5;
            case "Juni":
                return 6;
            case "Juli":
                return 7;
            case "Agustus":
                return 8;
            case "September":
                return 9;
            case "Oktober":
                return 10;
            case "November":
                return 11;
            case "Desember":
                return 12;
            default:
                return 0;
        }
    }
}