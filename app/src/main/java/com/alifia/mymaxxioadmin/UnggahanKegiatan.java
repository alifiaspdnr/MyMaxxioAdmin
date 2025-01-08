package com.alifia.mymaxxioadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alifia.mymaxxioadmin.model.Post;
import com.alifia.mymaxxioadmin.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UnggahanKegiatan extends AppCompatActivity {

    private MaterialToolbar btnKembali;
    private RecyclerView unggahanEventList;
    private Spinner btnTingkatanUnggahan;
    private PostAdapter postAdapter;
    private PostDiterimaAdapter postDiterimaAdapter;
    private PostDitolakAdapter postDitolakAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unggahan_kegiatan);

        btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // untuk menampilkan list unggahan kegiatan, digunakan recyclerview
        unggahanEventList = findViewById(R.id.unggahanEventList);
        unggahanEventList.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(new ArrayList<>()); // perlu ditinjau (0)
        postDiterimaAdapter = new PostDiterimaAdapter(new ArrayList<>()); // diterima (1)
        postDitolakAdapter = new PostDitolakAdapter(new ArrayList<>()); // ditolak (2)


        // inisialisasi spinner
        btnTingkatanUnggahan = findViewById(R.id.dropdown_unggahankeg);
        ArrayAdapter<CharSequence> adapterTingkatanEvent = ArrayAdapter.createFromResource(
                this,
                R.array.unggahan_keg_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btnTingkatanUnggahan.setAdapter(adapterTingkatanEvent);
        btnTingkatanUnggahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = btnTingkatanUnggahan.getSelectedItem().toString();
                int isPostedValue = getIsPostedValue(selectedStatus);
                loadPosts(isPostedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getIsPostedValue(String status) {
        switch (status) {
            case "Perlu Ditinjau":
                return 0;
            case "Diterima":
                return 1;
            case "Ditolak":
                return 2;
            default:
                return 0;
        }
    }

    private void loadPosts(int isPostedValue) {
        firestore = FirebaseFirestore.getInstance();

        if (isPostedValue == 1) { //diterima
            Query query = firestore.collection("posts").whereEqualTo("isPosted", isPostedValue);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Handle error
                        return;
                    }
                    if (snapshot != null) {
                        List<Post> posts = snapshot.toObjects(Post.class);
                        postDiterimaAdapter.updatePosts(posts);
                        unggahanEventList.setAdapter(postDiterimaAdapter);
                    }
                }
            });
        } else if (isPostedValue == 2) { //ditolak
            Query query = firestore.collection("posts").whereEqualTo("isPosted", isPostedValue);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Handle error
                        return;
                    }
                    if (snapshot != null) {
                        List<Post> posts = snapshot.toObjects(Post.class);
                        postDitolakAdapter.updatePosts(posts);
                        unggahanEventList.setAdapter(postDitolakAdapter);
                    }
                }
            });
        } else { //PERLU DITINJAU
            Query query = firestore.collection("posts").whereEqualTo("isPosted", isPostedValue);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        // Handle error
                        return;
                    }
                    if (snapshot != null) {
                        List<Post> posts = snapshot.toObjects(Post.class);
                        postAdapter.updatePosts(posts);
                        unggahanEventList.setAdapter(postAdapter);
                    }
                }
            });
        }
    }
}