package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostGridAdapter extends RecyclerView.Adapter<PostGridAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPosts;

    public PostGridAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item , parent , false);
        return new PostGridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        Picasso.get()
               .load(post.getPostImageUrl())
               .placeholder(R.drawable.ic_add_photo_png)
               .into(holder.postImageImageView);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView postImageImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageImageView = itemView.findViewById(R.id.postImageImageView);
        }
    }
}
