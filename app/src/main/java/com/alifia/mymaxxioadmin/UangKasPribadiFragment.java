package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class UangKasPribadiFragment extends Fragment {

    private RecyclerView recyclerView;
    private PencarianMemberAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference memberRef;
    private TextInputEditText searchEditText;
    private TextInputLayout textInputLayout;
    private ImageButton btnAddKas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uang_kas_pribadi, container, false);

        db = FirebaseFirestore.getInstance();
        memberRef = db.collection("member");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PencarianMemberAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        searchEditText = view.findViewById(R.id.search);
        textInputLayout = view.findViewById(R.id.textinput_search);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchMembers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnAddKas = view.findViewById(R.id.button_add);
        btnAddKas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TambahKasMember.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void searchMembers(String string) {
        memberRef.
                whereGreaterThanOrEqualTo("namaLengkap", string).
                whereLessThanOrEqualTo("namaLengkap", string + "\uf8ff").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> memberList = task.getResult().getDocuments();
                            adapter.setMemberList(memberList);
                        }
                    }
                });
    }
}