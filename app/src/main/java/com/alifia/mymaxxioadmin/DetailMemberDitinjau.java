package com.alifia.mymaxxioadmin;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DetailMemberDitinjau extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    private ImageView fotoKta;
    private TextView idAnggota, nama, email, noHp, regional, chapter, nopol;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    Button btnTerima, btnTolak;
    private String emailMember, idAnggotaMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_member_ditinjau);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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

        // mengeluarkan data intent extra dari MemberSemuaAdapter
        String namaMember = getIntent().getStringExtra("namaMember");
        Log.d("namaMember nyaaa : ", namaMember);
        if (namaMember != null) {
            // menampilkan data dari collection 'users_non_registered'
            // kemudian di-set text dengan isi datanya
            setupFirestore(namaMember);
        }

        btnTerima = findViewById(R.id.button_terima);
        btnTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bikin generate password -> munculin pop up isinya hasil generate password
                // -> regisin user ke auth -> push data ke collection 'users'
                // -> update status di users_non_registered menjadi "Diterima"
                // -> // masukin data ke collection 'member'
                terimaRegis(namaMember);
            }
        });

        btnTolak = findViewById(R.id.button_tolak);
        btnTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRejectionDialog(namaMember);
            }
        });
    }

    private void terimaRegis(String namaMember) {
        db.collection("users_non_registered")
                .whereEqualTo("namaLengkap", namaMember)
                .whereEqualTo("status", "Belum Ditinjau")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        emailMember = document.getString("email");
                        idAnggotaMember = document.getString("idAnggota");

                        String generatedPassword = generatePassword();
                        showPasswordDialog(generatedPassword, emailMember, idAnggotaMember);
                    } else {
                        Toast.makeText(DetailMemberDitinjau.this, "Data not found or already reviewed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPasswordDialog(String generatedPassword, String emailMember, String idAnggotaMember) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Generate Password");
        builder.setMessage("Generated Password: " + generatedPassword);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Proceed with registration
            registerUser(emailMember, generatedPassword, idAnggotaMember);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void registerUser(String emailMember, String generatedPassword, String idAnggotaMember) {
        // ke authentication buat daftarin user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailMember, generatedPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(idAnggotaMember)
                                .build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String userID = firebaseUser.getUid();
                                String idMemberUser = firebaseUser.getDisplayName();
                                String emailUser = firebaseUser.getEmail();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("userID", userID);
                                userMap.put("idMemberUser", idMemberUser);
                                userMap.put("photoProfile", "");
                                userMap.put("emailUser", emailUser);

                                // push data ke collection 'users'
                                db = FirebaseFirestore.getInstance();
                                CollectionReference usersCollectionRef = db.collection("users");

                                usersCollectionRef.document(firebaseUser.getUid()).set(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                // update status di users_non_registered menjadi "Diterima"
                                                db.collection("users_non_registered")
                                                        .whereEqualTo("idAnggota", idMemberUser)
                                                        .get()
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                                                // mulai
                                                                db.collection("users_non_registered").document(document.getId())
                                                                        .update("status", "Diterima")
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            Toast.makeText(DetailMemberDitinjau.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Toast.makeText(DetailMemberDitinjau.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                                                                        });
                                                            } else {
                                                                Toast.makeText(DetailMemberDitinjau.this, "Data not found or already reviewed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                //updateStatus(idMemberUser);

                                                // masukin data ke collection 'member'
                                                insertToCollectionMember(idMemberUser);

                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Register gagal!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*auth.createUserWithEmailAndPassword(emailMember, generatedPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userID = firebaseUser.getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", emailMember);
                            userMap.put("idMember", idAnggotaMember);
                            userMap.put("photoProfile", "");
                            userMap.put("userID", userID);

                            db.collection("users").document(userID)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        updateStatus(document.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(DetailMemberDitinjau.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(DetailMemberDitinjau.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    private void insertToCollectionMember(String idMemberUser) {
        db.collection("users_non_registered")
                .whereEqualTo("idAnggota", idMemberUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Prepare data to insert into member collection
                        Map<String, Object> memberData = new HashMap<>();
                        memberData.put("email", document.getString("email"));
                        memberData.put("idAnggota", document.getString("idAnggota"));
                        memberData.put("namaChapter", document.getString("namaChapter"));
                        memberData.put("namaLengkap", document.getString("namaLengkap"));
                        memberData.put("namaRegional", document.getString("namaRegional"));
                        memberData.put("noHp", document.getString("noHp"));
                        memberData.put("nopol", document.getString("nopol"));

                        // Add to member collection with auto-generated ID
                        db.collection("member")
                                .add(memberData)
                                .addOnSuccessListener(documentReference -> {
                                    // Update the document with auto-generated ID
                                    String memberId = documentReference.getId();
                                    documentReference.update("id", memberId)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("DetailMemberDitinjau", "DocumentSnapshot added with ID: " + memberId);
                                                Toast.makeText(DetailMemberDitinjau.this, "Data member added successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("DetailMemberDitinjau", "Error updating document", e);
                                                Toast.makeText(DetailMemberDitinjau.this, "Failed to add member data", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("DetailMemberDitinjau", "Error adding document", e);
                                    Toast.makeText(DetailMemberDitinjau.this, "Failed to add member data", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(DetailMemberDitinjau.this, "Failed to fetch non-registered user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private void showRejectionDialog(String namaMember) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tolak Registrasi");

        final EditText input = new EditText(this);
        input.setHint("Masukkan alasan penolakan");

        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(DetailMemberDitinjau.this, "Alasan penolakan harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // update ke collection 'users_non_registered' isi field 'alasan' menjadi "Ditolak"
                db.collection("users_non_registered")
                        .whereEqualTo("namaLengkap", namaMember)
                        .whereEqualTo("status", "Belum Ditinjau")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                db.collection("users_non_registered")
                                        .document(document.getId())
                                        .update("status", "Ditolak", "alasan", reason)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DetailMemberDitinjau.this, "Registrasi ditolak", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DetailMemberDitinjau.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(DetailMemberDitinjau.this, "Data tidak ditemukan atau sudah ditinjau", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setupFirestore(String namaMember) {
        db.collection("users_non_registered")
                .whereEqualTo("namaLengkap", namaMember)
                .whereEqualTo("status", "Belum Ditinjau")
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

                            String imageUrl = document.getString("fotoKTA");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                fotoKta.setVisibility(View.VISIBLE);
                                Glide.with(DetailMemberDitinjau.this)
                                        .load(imageUrl)
                                        .into(fotoKta);
                            }
                        } else {
                            Toast.makeText(DetailMemberDitinjau.this, "Data not found for: " + namaMember, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the error
                        Toast.makeText(DetailMemberDitinjau.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatus(String idMember) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", "Diterima");

        db.collection("users_non_registered").document(idMember).update(updateData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailMemberDitinjau.this, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        //finish();
                    } else {
                        Toast.makeText(DetailMemberDitinjau.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                    }
                });

        /*db.collection("users_non_registered")
                .whereEqualTo("idAnggota", idMember)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        db.collection("users_non_registered").document(document.getId())
                                .update("status", "Diterima")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(DetailMemberDitinjau.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(DetailMemberDitinjau.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(DetailMemberDitinjau.this, "Data not found or already reviewed", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }
}