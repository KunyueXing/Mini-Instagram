package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImageImageView;
        public ImageView postImageImageView;
        public ImageView likesImageView;
        public ImageView commentImageView;
        public ImageView moreImageView;
        public TextView usernameTextView;
        public TextView likesTextView;
        public TextView authorTextView;
        public SocialTextView descriptionTextView;
        public TextView commentTextView;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageImageView = itemView.findViewById(R.id.profile_image);
            postImageImageView = itemView.findViewById(R.id.post_image);
            likesImageView = itemView.findViewById(R.id.likes);
            commentImageView = itemView.findViewById(R.id.comment);
            moreImageView = itemView.findViewById(R.id.more_options);

            usernameTextView = itemView.findViewById(R.id.username);
            likesTextView = itemView.findViewById(R.id.num_likes);
            authorTextView = itemView.findViewById(R.id.author);
            descriptionTextView = itemView.findViewById(R.id.description);
            commentTextView = itemView.findViewById(R.id.comments_text);
        }
    }
}
