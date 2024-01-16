package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.fragments.PostDetailFragment;
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

        checkPostDetail(holder, post.getPostID());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    /**
     * When click on the photo, go to the post detail page.
     * @param holder
     * @param postID
     */
    private void checkPostDetail(@NonNull ViewHolder holder, String postID) {
        holder.postImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postID", postID);
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, new PostDetailFragment())
                                            .commit();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView postImageImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageImageView = itemView.findViewById(R.id.postImageImageView);
        }
    }
}
