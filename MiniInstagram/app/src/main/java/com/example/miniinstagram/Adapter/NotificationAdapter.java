package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.fragments.PostDetailFragment;
import com.example.miniinstagram.fragments.ProfileFragment;
import com.example.miniinstagram.model.Notification;
import com.example.miniinstagram.model.Post;
import com.example.miniinstagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotificationsList;
    private DatabaseReference databaseRef;
    private String databaseUsers = "Users";
    private String databasePosts = "Posts";
    private String TAG = "Notification Adapter: ";

    public NotificationAdapter(Context mContext, List<Notification> notificationsList) {
        this.mContext = mContext;
        this.mNotificationsList = notificationsList;
        databaseRef = FirebaseDatabase.getInstance().getReference();
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

        // If the notification is about a post, show post image in the item
        if (notification.isPost()) {
            holder.postImageImageview.setVisibility(View.VISIBLE);
            getPostImage(holder, notification.getPostID());
        } else {
            holder.postImageImageview.setVisibility(View.GONE);
        }

        clickNotificationItem(holder, notification);
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    /**
     * When click on the notification item, if it's about a like or a comment, go to the related
     * post detail page. If it's about a new follower, go to that user's profile page.
     * @param holder
     * @param notification
     */
    private void clickNotificationItem(@NonNull ViewHolder holder, Notification notification) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isPost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                                                              .edit();
                    editor.putString("postID", notification.getPostID());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container ,
                                                         new PostDetailFragment()).commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .edit();
                    editor.putString("profileUserID", notification.getUserID());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container ,
                                                         new ProfileFragment()).commit();
                }
            }
        });
    }

    /**
     * Get the post image from database and show in the notification item
     * @param holder
     * @param postID
     */
    private void getPostImage(@NonNull ViewHolder holder, String postID) {
        DatabaseReference ref = databaseRef.child(databasePosts).child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                assert post != null;
                Picasso.get().load(post.getPostImageUrl()).into(holder.postImageImageview);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get the post image", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Retrieve info of user included in the notification from database and show them in the
     * notification item
     *
     * @param holder
     * @param userID
     */
    private void getUserInfo(@NonNull ViewHolder holder, String userID) {
        DatabaseReference ref = databaseRef.child(databaseUsers).child(userID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Picasso.get()
                       .load(user.getProfilePicUriStr())
                       .placeholder(R.drawable.default_avatar)
                       .into(holder.profileImageImageview);
                holder.usernameTextView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get the user", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
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
