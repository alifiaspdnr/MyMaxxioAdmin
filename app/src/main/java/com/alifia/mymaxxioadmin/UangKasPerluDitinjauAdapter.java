package com.alifia.mymaxxioadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UangKasPerluDitinjauAdapter extends RecyclerView.Adapter<UangKasPerluDitinjauAdapter.UangKasPerluDitinjauViewHolder> {

    private List<DocumentSnapshot> uangkasList;
    Context context;

    public UangKasPerluDitinjauAdapter(List<DocumentSnapshot> uangkasList, Context context) {
        this.uangkasList = uangkasList;
        this.context = context;
    }

    @NonNull
    @Override
    public UangKasPerluDitinjauViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uangkas_ditinjau_row, parent, false);
        return new UangKasPerluDitinjauViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UangKasPerluDitinjauAdapter.UangKasPerluDitinjauViewHolder holder, int position) {
        DocumentSnapshot uangkas = uangkasList.get(position);
        holder.nama.setText(uangkas.getString("namaMember"));
        holder.bulan.setText(uangkas.getString("bulan"));
        holder.tahun.setText(uangkas.getString("tahun"));
        //holder.jumlah.setText(uangkas.get("jumlah_uang_kas").toString());

        // mengambil jumlah_uang_kas dan memformatnya jadi 10.000
        Number jumlahUangKas = uangkas.getDouble("jumlah_uang_kas");
        String formattedJumlah = NumberFormat.getNumberInstance(Locale.US).format(jumlahUangKas);

        holder.jumlah.setText(formattedJumlah);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailUangKasDitinjau.class);
                intent.putExtra("idKas", uangkas.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uangkasList.size();
    }

    public void setUangkasList(List<DocumentSnapshot> uangkasList) {
        this.uangkasList = uangkasList;
        notifyDataSetChanged();
    }

    static class UangKasPerluDitinjauViewHolder extends RecyclerView.ViewHolder {
        TextView nama, bulan, tahun, jumlah;

        public UangKasPerluDitinjauViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
            bulan = itemView.findViewById(R.id.bulan);
            jumlah = itemView.findViewById(R.id.jumlah);
            tahun = itemView.findViewById(R.id.tahun);
        }
    }
}
