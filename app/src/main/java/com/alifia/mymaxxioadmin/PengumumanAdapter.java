package com.alifia.mymaxxioadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alifia.mymaxxioadmin.model.Pengumuman;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.PengumumanViewHolder> {
    private List<DocumentSnapshot> pengumumanList;

    public PengumumanAdapter(List<DocumentSnapshot> pengumumanList) {
        this.pengumumanList = pengumumanList;
    }

    @NonNull
    @Override
    public PengumumanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengumuman, parent, false);
        return new PengumumanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PengumumanViewHolder holder, int position) {
        DocumentSnapshot pengumuman = pengumumanList.get(position);
        holder.judulPengumuman.setText(pengumuman.getString("judulPengumuman"));
        holder.isiPengumuman.setText(pengumuman.getString("isiPengumuman"));
    }

    @Override
    public int getItemCount() {
        return pengumumanList.size();
    }

    public void setPengumumanList(List<DocumentSnapshot> pengumumanList) {
        this.pengumumanList = pengumumanList;
        notifyDataSetChanged();
    }

    static class PengumumanViewHolder extends RecyclerView.ViewHolder {
        TextView judulPengumuman, isiPengumuman;

        public PengumumanViewHolder(@NonNull View itemView) {
            super(itemView);
            judulPengumuman = itemView.findViewById(R.id.judul_pengumuman);
            isiPengumuman = itemView.findViewById(R.id.isi_pengumuman);
        }
    }
}

//public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.PengumumanViewHolder> {
//    private List<Pengumuman> pengumumanList;
//
//    public PengumumanAdapter(List<Pengumuman> pengumumanList) {
//        this.pengumumanList = pengumumanList;
//    }
//
//    @Override
//    public PengumumanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengumuman, parent, false);
//        return new PengumumanViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(PengumumanViewHolder holder, int position) {
//        Pengumuman pengumuman = pengumumanList.get(position);
//        holder.judulPengumuman.setText(pengumuman.getJudulPengumuman());
//        holder.isiPengumuman.setText(pengumuman.getIsiPengumuman());
//        // Set other fields as needed
//    }
//
//    @Override
//    public int getItemCount() {
//        return pengumumanList.size();
//    }
//
//    public static class PengumumanViewHolder extends RecyclerView.ViewHolder {
//        public TextView judulPengumuman;
//        public TextView isiPengumuman;
//
//        public PengumumanViewHolder(View itemView) {
//            super(itemView);
//            judulPengumuman = itemView.findViewById(R.id.judul_pengumuman);
//            isiPengumuman = itemView.findViewById(R.id.isi_pengumuman);
//        }
//    }
//}

