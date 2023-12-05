package com.example.miniinstagram.fragments;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.Value;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MypageFragment extends Fragment {

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

    private String profileID;

    private String TAG = "User profile fragment: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Posts_Image");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profileID = fbUser.getUid();

        profileImageCircleImageView = view.findViewById((R.id.profile_image));
        optionsImageView = view.findViewById(R.id.options);
        myPostsImageView = view.findViewById(R.id.my_posts);

        postsTextView = view.findViewById(R.id.posts);
        followersTextView = view.findViewById(R.id.followers);
        followingTextView = view.findViewById(R.id.following);
        nameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        usernameTextView = view.findViewById(R.id.username);

        showUserBasicContent();


        return view;
    }

    // Fetch and show basic content of user profile. such as info, number of followers and following, user posts, etc
    private void showUserBasicContent() {
        getUserInfo();
    }

    private void getUserInfo() {
        DatabaseReference usersReference = databaseReference.child("Users").child(profileID);

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

                usersReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
                usersReference.removeEventListener(this);
            }
        };

        usersReference.addValueEventListener(listener);
    }
}