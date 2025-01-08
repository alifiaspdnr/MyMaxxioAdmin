package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alifia.mymaxxioadmin.model.Post;
import com.alifia.mymaxxioadmin.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailUnggahanKegiatanDitolak extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    ImageView imgUserPhoto;
    TextView username, isiPost, namaKegiatan, alasan;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_unggahan_kegiatan_ditolak);

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ambil data bundle (id post) dari PostAdapter
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String bundlePostID = bundle.getString("postId");
        Log.d("ISI BUNDLE APA???", bundlePostID);

        imgUserPhoto = findViewById(R.id.img_user_photo);
        username = findViewById(R.id.tv_username);
        isiPost = findViewById(R.id.tv_description);
        namaKegiatan = findViewById(R.id.tv_nama_kegiatan);
        alasan = findViewById(R.id.alasan);

        db = FirebaseFirestore.getInstance();

        if (bundlePostID != null) {
            db.collection("posts")
                    .document(bundlePostID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            Post posts = documentSnapshot.toObject(Post.class);

                            // ngeset foto profil
                            FirebaseFirestore store = FirebaseFirestore.getInstance();
                            store
                                    .collection("users")
                                    .document(posts.getIdAnggota())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User user = documentSnapshot.toObject(User.class);
                                            if (user.getPhotoProfile() != null) {
                                                Glide.with(getApplicationContext())
                                                        .load(user.getPhotoProfile())
                                                        .into(imgUserPhoto);
                                            } else {
                                                imgUserPhoto.setImageResource(R.drawable.ic_profile);
                                            }
                                        }
                                    });

                            username.setText(posts.getNama());
                            isiPost.setText(posts.getIsiPost());
                            alasan.setText(posts.getAlasan());

                            String idKegiatan = posts.getIdKegiatan();
                            store.collection("kegiatan").whereEqualTo("id", idKegiatan).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                                String strNamaKegiatan = document.getString("namaKegiatan");
                                                namaKegiatan.setText(strNamaKegiatan);
                                            } else {
                                                Log.d("MAAF GABISA", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    });
        }
    }
}