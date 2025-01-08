package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UangKasChapterFragment extends Fragment {

    private Spinner btnTingkatanChapter;
    private FirebaseFirestore db;
    private ImageButton btnAddKasChapter;
    private CollectionReference kasChapterRef;
    private RecyclerView kasChapterList;
    private KasChapterAdapter adapter;
    private Spinner btnDropdownChapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uang_kas_chapter, container, false);

        db = FirebaseFirestore.getInstance();
        kasChapterRef = db.collection("kas_chapter");

        // untuk menampilkan list chapter, digunakan recyclerview
        kasChapterList = view.findViewById(R.id.uangkasList);
        kasChapterList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new KasChapterAdapter(new ArrayList<>(), getContext());
        kasChapterList.setAdapter(adapter);

        btnAddKasChapter = view.findViewById(R.id.button_add);
        btnAddKasChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TambahKasChapter.class);
                startActivity(intent);
            }
        });

        btnDropdownChapter = view.findViewById(R.id.dropdown_chapter);
        ArrayAdapter<CharSequence> adapterTingkatanPengumuman = ArrayAdapter.createFromResource(
                getContext(),
                R.array.kas_chapter_array,
                R.layout.custom_spinner_item
        );
        adapterTingkatanPengumuman.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btnDropdownChapter.setAdapter(adapterTingkatanPengumuman);
        btnDropdownChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedChapter = btnDropdownChapter.getSelectedItem().toString();
                setupFirestore(selectedChapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setupFirestore("Jakarta Raya");

        return view;
    }

    private void setupFirestore(String namaChapter) {
        kasChapterRef.
                whereEqualTo("chapter", namaChapter).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> kasChapterList = task.getResult().getDocuments();
                            adapter.setKasChapterList(kasChapterList);
                        }
                    }
                });
    }
}