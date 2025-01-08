package com.alifia.mymaxxioadmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MemberDitolakFragment extends Fragment {

    private RecyclerView recyclerView;
    private MemberDitolakAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference memberDitolakRef;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_ditolak, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        memberDitolakRef = db.collection("users_non_registered");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        memberDitolakRef.
                whereEqualTo("status", "Ditolak").
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> memberDitolakList = task.getResult().getDocuments();
                            adapter = new MemberDitolakAdapter(new ArrayList<>(), getContext());
                            recyclerView.setAdapter(adapter);
                            adapter.setMemberDitolakList(memberDitolakList);
                        }
                    }
                });

        return view;
    }
}