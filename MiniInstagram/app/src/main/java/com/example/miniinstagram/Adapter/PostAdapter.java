package com.example.miniinstagram.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;

public class PostAdapter {
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
        }
    }
}
