package com.alifia.mymaxxioadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEvent extends AppCompatActivity {
    private MaterialToolbar btnKembali;
    private EditText namaKegiatan, desc_singkat, desc_kegiatan, tanggal;
    private String strNamaKegiatan, strTingkatan, strRegional, strChapter,
            strDescSingkat, strDescKegiatan, strTanggal;
    private Spinner tingkatan, regional, chapter;
    private Button btnSubmit;
    private LinearLayout btnUploadImage;
    private FirebaseFirestore db;
    private static final int PICK_MEDIA_REQUEST = 1;
    private Uri mediaUri;
    private ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();

        eventImage = findViewById(R.id.eventImage);

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUploadImage = findViewById(R.id.btn_upload_fotoevent);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMediaPicker();
            }
        });

        namaKegiatan = findViewById(R.id.nama_kegiatan);
        desc_singkat = findViewById(R.id.desc_singkat);
        desc_kegiatan = findViewById(R.id.desc_kegiatan);

        tanggal = findViewById(R.id.tanggal);
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tingkatan = findViewById(R.id.dropdown_tingkatan);
        ArrayAdapter<CharSequence> adapterTingkatanReqEvent = ArrayAdapter.createFromResource(
                this,
                R.array.req_event_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanReqEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tingkatan.setAdapter(adapterTingkatanReqEvent);
        tingkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regional = findViewById(R.id.dropdown_regional);
        ArrayAdapter<CharSequence> adapterRegionalReqEvent = ArrayAdapter.createFromResource(
                this,
                R.array.req_event_regional_array,
                R.layout.custom_spinner_item
        );
        adapterRegionalReqEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regional.setAdapter(adapterRegionalReqEvent);
        regional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chapter = findViewById(R.id.dropdown_chapter);
        ArrayAdapter<CharSequence> adapterChapterReqEvent = ArrayAdapter.createFromResource(
                this,
                R.array.req_event_chapter_array,
                R.layout.custom_spinner_item
        );
        adapterChapterReqEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chapter.setAdapter(adapterChapterReqEvent);
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
                try {
                    submitRequest();
                } catch (ParseException e) {
                    Log.e("TAG", "Parse error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK && data != null) {
            mediaUri = data.getData();
            Log.d("TAG", "onActivityResult: " + mediaUri);
            eventImage.setImageURI(mediaUri);
            eventImage.setVisibility(View.VISIBLE);

            // Do something with the mediaUri if needed
        }
    }

    private void openMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
//                        tanggal.setText(selectedDate);

                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tanggal.setText(selectedDate);

//                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                        try {
//                            Date date = sdf.parse(selectedDate);
//                            if (date != null) {
//                                tglTimestamp = new Timestamp(date);
//                            }
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void submitRequest() throws ParseException {
        strNamaKegiatan = namaKegiatan.getText().toString();
        strTingkatan = tingkatan.getSelectedItem().toString();
        strRegional = regional.getSelectedItem().toString();
        strChapter = chapter.getSelectedItem().toString();
        strDescSingkat = desc_singkat.getText().toString();
        strDescKegiatan = desc_kegiatan.getText().toString();
        String strTanggal = tanggal.getText().toString();
        //foto

        if (strNamaKegiatan.isEmpty() || strTingkatan.isEmpty() || strRegional.isEmpty() || strChapter.isEmpty() || strDescSingkat.isEmpty() || strDescKegiatan.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        Date date = null;
//        try {
//            date = sdf.parse(strTanggal);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Timestamp timestamp = new Timestamp(date);

        Map<String, Object> dataRequestEvent = new HashMap<>();
        dataRequestEvent.put("namaKegiatan", strNamaKegiatan);
        dataRequestEvent.put("namaTingkatan", strTingkatan);
        dataRequestEvent.put("namaRegional", strRegional);
        dataRequestEvent.put("namaChapter", strChapter);
        dataRequestEvent.put("deskripsiSingkat", strDescSingkat);
        dataRequestEvent.put("deskripsi", strDescKegiatan);

//        if (tglTimestamp != null) {
//            dataRequestEvent.put("tgl", tglTimestamp);
//        }

        //tanggal
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = dateFormat.parse(strTanggal);
        assert date != null;
        Timestamp tmp = new Timestamp(date);
        dataRequestEvent.put("tgl", tmp);

        dataRequestEvent.put("isApproved", 1);

        DocumentReference newEventRef = db.collection("kegiatan").document();
        dataRequestEvent.put("id", newEventRef.getId());

        // kalo isi post ada media
        if (mediaUri != null) {
            // fungsi untuk panggil instance storage (untuk nyimpen media)
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            StorageReference imageRef = storageRef.child("event" + "/" + newEventRef.getId());
            UploadTask uploadTask = imageRef.putFile(mediaUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnCompleteListener(imageTask -> {
                        if (imageTask.isSuccessful()) {
                            String url = imageTask.getResult().toString();
                            dataRequestEvent.put("imageUrl", url);
                            newEventRef
                                    .set(dataRequestEvent)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            showConfirmationDialog();
                                        } else {
                                            Toast.makeText(AddEvent.this, "Event gagal terkirim", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        } else { // kalo isi event gaada media (cuma teks aja)
            newEventRef
                    .set(dataRequestEvent)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showConfirmationDialog();
                        } else {
                            Toast.makeText(AddEvent.this, "Event gagal terkirim", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Kegiatan berhasil dikirim!")
                .setPositiveButton("Oke.", (dialog, which) -> {
                    finish();
                })
                .show();
    }
}