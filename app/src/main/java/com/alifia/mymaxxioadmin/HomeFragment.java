package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private RelativeLayout btnMember, btnUnggahanKegiatan, btnKegiatan, btnUangkas, btnPengumuman;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        btnMember = rootView.findViewById(R.id.btn_member);
        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Member.class);
                startActivity(intent);
            }
        });

        btnUnggahanKegiatan = rootView.findViewById(R.id.btn_unggahankegiatan);
        btnUnggahanKegiatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UnggahanKegiatan.class);
                startActivity(intent);
            }
        });

        btnKegiatan = rootView.findViewById(R.id.btn_kegiatan);
        btnKegiatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Kegiatan.class);
                startActivity(intent);
            }
        });

        btnUangkas = rootView.findViewById(R.id.btn_uangkas);
        btnUangkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UangKas.class);
                startActivity(intent);
            }
        });

        btnPengumuman = rootView.findViewById(R.id.btn_pengumuman);
        btnPengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PengumumanActivity.class);
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