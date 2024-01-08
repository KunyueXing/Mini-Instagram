package com.example.miniinstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.example.miniinstagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView profileImageCircleImageView;
    private ImageView optionsImageView;
    private ImageView myPostsImageView;

    private TextView postsTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private TextView nameTextView;
    private TextView bioTextView;
    private TextView usernameTextView;

    private FirebaseUser fbUser;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String profileUserID;

    private List<Post> postList;

    private String TAG = "Profile fragment: ";
    private String storagePostsImage = "Posts_Image";
    private String databaseUserPosts = "User-Posts";
    private String databaseUsers = "Users";
    private String databaseFollowing = "User-following";
    private String databaseFollowedby = "User-followedby";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileUserID = fbUser.getUid();

        profileImageCircleImageView = view.findViewById((R.id.profile_image));
        optionsImageView = view.findViewById(R.id.options);
        myPostsImageView = view.findViewById(R.id.my_posts);

        postsTextView = view.findViewById(R.id.posts);
        followersTextView = view.findViewById(R.id.followers);
        followingTextView = view.findViewById(R.id.following);
        nameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        usernameTextView = view.findViewById(R.id.username);

        postList = new ArrayList<>();

        showUserBasicContent();

        return view;
    }

    /**
     * Fetch and show basic content of user profile. such as username, bio, number of followers
     * and following, user posts, etc
     */
    private void showUserBasicContent() {
        getUserInfo();

        getFollowerNum();
        getFollowingNum();

        getNumOfPosts();
    }

    /**
     * Get the number of followers the profile owner has.
     */
    private void getFollowerNum() {
        DatabaseReference ref = databaseReference.child(databaseFollowedby).child(profileUserID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersTextView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Fail to get followers number", error.toException());
            }
        };
        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Get the number of users the profile owner is following.
     */
    private void getFollowingNum() {
        DatabaseReference ref = databaseReference.child(databaseFollowing).child(profileUserID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingTextView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Fail to get following number", error.toException());
            }
        };
        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Get the number of posts the profile owner had posted
     */
    private void getNumOfPosts() {
        DatabaseReference postsRef = databaseReference.child(databaseUserPosts).child(profileUserID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsTextView.setText(String.valueOf(snapshot.getChildrenCount()));

                postsRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value when get posts from user-posts.", error.toException());
                postsRef.removeEventListener(this);
            }
        };

        postsRef.addValueEventListener(listener);
    }

    // Retrieve username， name， bio from database and show them on user profile
    private void getUserInfo() {
        DatabaseReference usersRef = databaseReference.child(databaseUsers).child(profileUserID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);
                Picasso.get()
                       .load(user.getProfilePicUriStr())
                       .placeholder(R.drawable.default_avatar)
                       .into(profileImageCircleImageView);
//                Log.d(TAG, "onDataChange: username: " + user.getUsername());
//                Log.d(TAG, "onDataChange: email: " + user.getEmail());
//                Log.d(TAG, "onDataChange: status: " + user.getAccountStatus());
//                Log.d(TAG, "onDataChange: userId: " + user.getUserID());

                usernameTextView.setText(user.getUsername());

                if (user.getName() != null && user.getName().length() != 0) {
                    nameTextView.setText(user.getName());
                }

                if (user.getBio() != null && user.getBio().length() != 0) {
                    bioTextView.setText(user.getBio());
                }

                usersRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value when get user info from users.", error.toException());
                usersRef.removeEventListener(this);
            }
        };

        usersRef.addValueEventListener(listener);
    }
}