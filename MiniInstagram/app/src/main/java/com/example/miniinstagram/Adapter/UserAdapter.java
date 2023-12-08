package com.example.miniinstagram.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * UserAdapter requests views, and binds the views to their data, by calling methods in the adapter.
 * Define the adapter by extending RecyclerView.Adapter.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;
    private String TAG = "UserAdapter: ";

    /**
     * Initialize the dataset of the Adapter
     *
     * @param mUsers List<User> containing the data to populate views to be used
     * by RecyclerView
     * @param isFragment boolean is used to check if this adapter is used in a fragment or not.
     * Since we'll also use it in an activity
     * @param mContext
     */
    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    /**
     * Each individual element in the list is defined by a view holder object.
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImageCircleImageView;
        public TextView nameTextView;
        public TextView usernameTextView;
        public Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageCircleImageView = itemView.findViewById(R.id.profile_image);
            usernameTextView = itemView.findViewById(R.id.username);
            nameTextView = itemView.findViewById(R.id.name);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item , parent , false);
        return new UserAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.followButton.setVisibility(View.VISIBLE);
        holder.usernameTextView.setText(user.getUsername());
        holder.nameTextView.setText(user.getName());
        Picasso.get()
               .load(user.getProfilePicUriStr())
               .placeholder(R.drawable.default_avatar)
               .into(holder.profileImageCircleImageView);

        isFollowed(user.getUserID(), holder.followButton);

        // If user in search results is the current user herself, the follow button would disappear
        if (user.getUserID().equals(firebaseUser.getUid())) {
            holder.followButton.setVisibility(View.GONE);
        }
    }

    // Return the size of user list, which is the search results (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * To check if a user (its id is passed in as @param userID) is followed by the current user
     * and show corresponding results on button
     */
    private void isFollowed(final String userID, final Button button) {
        // firebaseUser.getUid() -- current user,
        // The users she is following are stored in "User-Following" and in a "default" group in database
        DatabaseReference userFollowingRef = FirebaseDatabase.getInstance()
                                                             .getReference()
                                                             .child("User-Following")
                                                             .child("default")
                                                             .child(firebaseUser.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userID).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }

                userFollowingRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value when get User-following", error.toException());
                userFollowingRef.removeEventListener(this);
            }
        };

        userFollowingRef.addValueEventListener(listener);
    }


}
