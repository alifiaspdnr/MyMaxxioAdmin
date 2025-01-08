package com.alifia.mymaxxioadmin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class KasMemberAdapter extends RecyclerView.Adapter<KasMemberAdapter.KasMemberViewHolder> {

    private List<DocumentSnapshot> kasMemberList;
    Context context;

    public KasMemberAdapter(List<DocumentSnapshot> kasMemberList, Context context) {
        this.kasMemberList = kasMemberList;
        this.context = context;
    }

    @NonNull
    @Override
    public KasMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftarkas_member, parent, false);
        return new KasMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KasMemberViewHolder holder, int position) {
        DocumentSnapshot kasMember = kasMemberList.get(position);
        holder.bulan.setText(kasMember.getString("bulan"));
        holder.tahun.setText(kasMember.getString("tahun"));

        holder.status.setText(kasMember.getString("status"));
        //holder.jumlah.setText(uangkas.get("jumlah_uang_kas").toString());

        // mengambil jumlah_uang_kas dan memformatnya jadi 10.000
        Number jumlahUangKas = kasMember.getDouble("jumlah_uang_kas");
        String formattedJumlah = NumberFormat.getNumberInstance(Locale.US).format(jumlahUangKas);

        holder.jumlah.setText(formattedJumlah);

        // buat bisa dipencet untuk yang statusnya ditolak/lunas/dalam review aja
        // belum lunas (blm bayar) gabisa dipencet
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strStatus = holder.status.getText().toString();
                if (strStatus.equals("Ditolak") || strStatus.equals("Lunas") || strStatus.equals("Dalam Review")) {
                    Intent intent = new Intent(v.getContext(), DetailUangKasMember.class);
                    /*intent.putExtra("namaMember", kasMember.getString("namaMember"));
                    intent.putExtra("status", strStatus);*/
                    intent.putExtra("idKas", kasMember.getId());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return kasMemberList.size();
    }

    public void setKasMemberList(List<DocumentSnapshot> kasMemberList) {
        this.kasMemberList = kasMemberList;
        notifyDataSetChanged();
    }

    static class KasMemberViewHolder extends RecyclerView.ViewHolder {
        TextView bulan, tahun, status, jumlah;

        public KasMemberViewHolder(@org.checkerframework.checker.nullness.qual.NonNull View itemView) {
            super(itemView);
            bulan = itemView.findViewById(R.id.bulan);
            tahun = itemView.findViewById(R.id.tahun);
            status = itemView.findViewById(R.id.status);
            jumlah = itemView.findViewById(R.id.jumlah);
        }
    }
}
