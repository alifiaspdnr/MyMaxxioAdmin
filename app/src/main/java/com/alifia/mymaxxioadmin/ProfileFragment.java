package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    private RelativeLayout btnGantiPassword, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        btnGantiPassword = rootView.findViewById(R.id.btn_ganti_pass);
        btnGantiPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GantiPassword.class);
                startActivity(intent);
            }
        });

        btnLogout = rootView.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // menampilkan alert konfirmasi apakah mau logout?
                new AlertDialog.Builder(getContext())
                        .setMessage("Apakah Anda yakin ingin logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            mAuth.signOut();
                            Intent intent = new Intent(getActivity(), Login.class);
                            startActivity(intent);
                        })
                        .setNeutralButton("Batal", null)
                        .show();
            }
        });

        return rootView;
    }
}