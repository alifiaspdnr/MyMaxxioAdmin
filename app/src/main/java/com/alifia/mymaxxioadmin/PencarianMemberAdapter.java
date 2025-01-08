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

import java.util.List;

public class PencarianMemberAdapter extends RecyclerView.Adapter<PencarianMemberAdapter.PencarianMemberViewHolder> {

    private List<DocumentSnapshot> memberList;
    Context context;

    public PencarianMemberAdapter(List<DocumentSnapshot> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }

    @NonNull
    @Override
    public PencarianMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kas_member, parent, false);
        return new PencarianMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PencarianMemberViewHolder holder, int position) {
        DocumentSnapshot dataMember = memberList.get(position);
        holder.nama.setText(dataMember.getString("namaLengkap"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DaftarUangKasMember.class);
                intent.putExtra("namaMember", dataMember.getString("namaLengkap"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void setMemberList(List<DocumentSnapshot> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    static class PencarianMemberViewHolder extends RecyclerView.ViewHolder {
        TextView nama;

        public PencarianMemberViewHolder(@org.checkerframework.checker.nullness.qual.NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
        }
    }
}
