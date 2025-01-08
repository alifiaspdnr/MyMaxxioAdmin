package com.alifia.mymaxxioadmin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TambahKasChapter extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    private static final int PICK_FILE_REQUEST = 1;
    private TextView namaFile;
    private Uri fileUri;
    private Spinner chapterSpinner, bulanSpinner, tahunSpinner;
    private LinearLayout uploadFileButton;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tambah_kas_chapter);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chapterSpinner = findViewById(R.id.dropdown_chapter);
        ArrayAdapter<CharSequence> chapterSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.kas_chapter_array,
                R.layout.custom_spinner_item
        );
        chapterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapterSpinner.setAdapter(chapterSpinnerAdapter);
        chapterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadFileButton = findViewById(R.id.btn_upload_file);
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        namaFile = findViewById(R.id.file_name_text);


        submitButton = findViewById(R.id.button_simpan);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            String fileName = fileUri.getLastPathSegment();
            namaFile.setText(fileName);
            //Toast.makeText(this, "File Selected: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        String bulan = bulanSpinner.getSelectedItem().toString();
        String tahun = tahunSpinner.getSelectedItem().toString();
        String chapter = chapterSpinner.getSelectedItem().toString();

        if (bulan.isEmpty() && tahun.isEmpty() && chapter.isEmpty() && fileUri == null) {
            Toast.makeText(this, "Lengkapi semua data dan pilih file.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageReference.child("kas_chapter/" + System.currentTimeMillis() + ".xlsx");
        fileReference.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                saveDataToFirestore(bulan, tahun, chapter, downloadUri.toString());
                                finish();
                            } else {
                                Toast.makeText(TambahKasChapter.this, "Failed to get download URL.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(TambahKasChapter.this, "File upload failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDataToFirestore(String bulan, String tahun, String chapter, String fileUrl) {
        int bulan_urutan = getBulanUrutan(bulan);

        Map<String, Object> kasChapterData = new HashMap<>();
        kasChapterData.put("bulan", bulan);
        kasChapterData.put("bulan_urutan", bulan_urutan);
        kasChapterData.put("tahun", tahun);
        kasChapterData.put("chapter", chapter);
        kasChapterData.put("fileUrl", fileUrl);

        db.collection("kas_chapter")
                .add(kasChapterData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String docId = documentReference.getId(); // Dapatkan ID dokumen
                        // Perbarui dokumen dengan field id
                        documentReference.update("id", docId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(TambahKasChapter.this, "Data saved successfully with ID.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TambahKasChapter.this, "Failed to save data ID.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahKasChapter.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                    }
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