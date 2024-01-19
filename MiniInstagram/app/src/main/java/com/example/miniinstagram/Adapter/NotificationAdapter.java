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
import com.example.miniinstagram.model.Notification;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotificationsList;
    private DatabaseReference databaseRef;
    private String databaseUsers = "Users";
    private String databasePosts = "Posts";

    public NotificationAdapter(Context mContext, List<Notification> notificationsList) {
        this.mContext = mContext;
        this.mNotificationsList = notificationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item , parent , false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotificationsList.get(position);

        holder.contentTextView.setText(notification.getContent());
        getUserInfo(holder, notification.getUserID());
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    private void getUserInfo(@NonNull ViewHolder holder, String userID) {
        
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImageImageview;
        public CircleImageView profileImageImageview;
        public TextView usernameTextView;
        public TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImageImageview = itemView.findViewById(R.id.postImageImageView);
            profileImageImageview = itemView.findViewById(R.id.profileImageImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }
    }
}
