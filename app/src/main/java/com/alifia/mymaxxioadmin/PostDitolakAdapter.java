package com.alifia.mymaxxioadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alifia.mymaxxioadmin.model.Post;
import com.alifia.mymaxxioadmin.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PostDitolakAdapter extends RecyclerView.Adapter<PostDitolakAdapter.PostDitolakViewHolder> {

    private List<Post> posts;
    private FirebaseFirestore store;
    private User user;

    public PostDitolakAdapter(List<Post> posts) {
        this.posts = posts;
        store = FirebaseFirestore.getInstance();
        this.user = new User();
    }

    public void updatePosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    class PostDitolakViewHolder extends RecyclerView.ViewHolder {

        ImageView userPhoto, postImage;
        TextView userName, description, category, namaKegiatan;

        public PostDitolakViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.img_user_photo);
            userName = itemView.findViewById(R.id.tv_username);
            postImage = itemView.findViewById(R.id.postImage);
            description = itemView.findViewById(R.id.tv_description);
            category = itemView.findViewById(R.id.tv_category);
            namaKegiatan = itemView.findViewById(R.id.tv_nama_kegiatan);

            // kalo postingan dipencet, intent ke DetailUnggahanKegiatanDitolak
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post data_post = posts.get(getAdapterPosition());
                    Log.d("ID POST CLICK DI CARD", data_post.getId());
                    Intent intent = new Intent(v.getContext(), DetailUnggahanKegiatanDitolak.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("postId", data_post.getId());
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public PostDitolakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_row, parent, false);
        return new PostDitolakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostDitolakAdapter.PostDitolakViewHolder holder, int position) {
        store
                .collection("users")
                .document(posts.get(position).getIdAnggota())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        if (user.getPhotoProfile() != null) {
                            Glide.with(holder.itemView.getContext())
                                    .load(user.getPhotoProfile())
                                    .into(holder.userPhoto);
                        } else {
                            holder.userPhoto.setImageResource(R.drawable.ic_profile);
                        }
                    }
                });

        holder.userName.setText(posts.get(position).getNama());
        holder.description.setText(posts.get(position).getIsiPost());
        holder.category.setText(posts.get(position).getChapter());

        if (posts.get(position).getAttachment() != null && posts.get(position).getAttachment() != "") {
            Glide.with(holder.itemView.getContext())
                    .load(posts.get(position).getAttachment())
                    .into(holder.postImage);
            holder.postImage.setVisibility(View.VISIBLE);
        }

        String idKegiatan = posts.get(position).getIdKegiatan();
        store.collection("kegiatan").whereEqualTo("id", idKegiatan).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String strNamaKegiatan = document.getString("namaKegiatan");
                            holder.namaKegiatan.setText(strNamaKegiatan);
                        } else {
                            Log.d("MAAF GABISA", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
