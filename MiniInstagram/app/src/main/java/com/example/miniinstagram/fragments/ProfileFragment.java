package com.example.miniinstagram.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.Adapter.PostGridAdapter;
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
    private Button editProfileButton;

    private RecyclerView recyclerView;
    private PostGridAdapter postGridAdapter;
    private List<Post> postGridList;

    private FirebaseUser fbUser;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String profileUserID;
    private SharedPreferences transferredID;
    private ValueEventListener followingNumListener;
    private ValueEventListener followedbyNumListener;
    private ValueEventListener postsNumListener;

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

        // If userData is not null. It stores a userID of whose profile will be visited.
        // If userData is null, go to user's own profile page.
        transferredID = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE);
        String transferredIDStr = transferredID.getString("profileUserID", "none");
        if (transferredIDStr.equals("none")) {
            profileUserID = fbUser.getUid();
        } else {
            profileUserID = transferredIDStr;
        }

        profileImageCircleImageView = view.findViewById((R.id.profile_image));
        optionsImageView = view.findViewById(R.id.options);
        myPostsImageView = view.findViewById(R.id.my_posts);

        postsTextView = view.findViewById(R.id.posts);
        followersTextView = view.findViewById(R.id.followers);
        followingTextView = view.findViewById(R.id.following);
        nameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        usernameTextView = view.findViewById(R.id.username);
        editProfileButton = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext() , 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postGridList = new ArrayList<>();
        postGridAdapter = new PostGridAdapter(getContext() , postGridList);
        recyclerView.setAdapter(postGridAdapter);

        recyclerView.setVisibility(View.VISIBLE);

        if (profileUserID.equals(fbUser.getUid())) {
            editProfileButton.setText("Edit profile");
        } else {
            checkFollowingStatus();
        }

        showUserBasicContent();
        editProfileOrFollow();
        getPostsList();

        return view;
    }

    private void getPostsList() {
        DatabaseReference ref = databaseReference.child(databaseUserPosts).child(profileUserID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postGridList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    Post post = subSnapshot.getValue(Post.class);
                    postGridList.add(post);
                }

                // Sort the list in some order

                postGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all posts", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * When user click on the edit profile button, if it shows following/follow, will execute
     * unfollow/follow action. If it shows edit profile, go to edit profile page.
     */
    private void editProfileOrFollow() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText = editProfileButton.getText().toString();

                if (btnText.equals("Edit profile")) {
                    // go to edit profile activity
                } else if (btnText.equals("Follow")) {
                    databaseReference.child(databaseFollowing)
                            .child(fbUser.getUid())
                            .child(profileUserID)
                            .setValue(true);
                    databaseReference.child(databaseFollowedby)
                            .child(profileUserID)
                            .child(fbUser.getUid())
                            .setValue(true);
                    editProfileButton.setText("Following");
                } else if (btnText.equals("Following")) {
                    databaseReference.child(databaseFollowing)
                            .child(fbUser.getUid())
                            .child(profileUserID)
                            .removeValue();
                    databaseReference.child(databaseFollowedby)
                            .child(profileUserID)
                            .child(fbUser.getUid())
                            .removeValue();
                    editProfileButton.setText("Follow");
                }
            }
        });
    }

    /**
     * When open others' profile, the edit profile button will act as follow button, showing
     * following status accordingly.
     */
    private void checkFollowingStatus() {
        String userID = fbUser.getUid();

        DatabaseReference ref = databaseReference.child(databaseFollowing).child(userID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileUserID).exists()) {
                    editProfileButton.setText("Following");
                } else {
                    editProfileButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get Following status", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
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

        followedbyNumListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersTextView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Fail to get followers number", error.toException());
            }
        };
        ref.addValueEventListener(followedbyNumListener);
    }

    /**
     * Get the number of users the profile owner is following.
     */
    private void getFollowingNum() {
        DatabaseReference ref = databaseReference.child(databaseFollowing).child(profileUserID);

        followingNumListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingTextView.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Fail to get following number", error.toException());
            }
        };
        ref.addValueEventListener(followingNumListener);
    }

    /**
     * Get the number of posts the profile owner had posted
     */
    private void getNumOfPosts() {
        DatabaseReference postsRef = databaseReference.child(databaseUserPosts).child(profileUserID);

        postsNumListener = new ValueEventListener() {
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

        postsRef.addValueEventListener(postsNumListener);
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

    @Override
    public void onStop() {
        super.onStop();

        if (followedbyNumListener != null) {
            databaseReference.child(databaseFollowedby)
                             .child(profileUserID)
                             .removeEventListener(followedbyNumListener);
        }

        if (followingNumListener != null) {
            databaseReference.child(databaseFollowing)
                             .child(profileUserID)
                             .removeEventListener(followingNumListener);
        }

        if (postsNumListener != null) {
            databaseReference.child(databaseUserPosts)
                             .child(profileUserID)
                             .removeEventListener(postsNumListener);
        }

        transferredID.edit().clear().commit();
    }
}