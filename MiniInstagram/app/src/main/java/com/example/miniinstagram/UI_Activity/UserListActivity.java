package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.Adapter.UserAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.example.miniinstagram.model.UserAdapterCode;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserListActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private String databaseUsers = "Users";
    private String databaseFollowing = "User-following";
    private String databaseLikes = "Likes";
    private String databaseFollowedby = "User-followedby";
    private String TAG = "User List Activity: ";
    private FirebaseUser user;

    private String userID;
    private String userListTitle;
    private String postID;
    private Set<String> userIDList;

    private RecyclerView recyclerView;
    private ImageView closeImageView;
    private TextView listTitleTextView;
    private UserAdapter userAdapter;
    private List<User> mUserList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();

        userListTitle = intent.getStringExtra("userListTitle");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserList = new ArrayList<>();
        userAdapter = new UserAdapter(this , mUserList , UserAdapterCode.USER_ADAPTER_CODE_GENERAL);
        recyclerView.setAdapter(userAdapter);

        closeImageView = findViewById(R.id.closeImageView);
        listTitleTextView = findViewById(R.id.listTitleTextView);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        userIDList = new HashSet<>();

        getList();
    }

    private void getList() {
        switch (userListTitle) {
            case "Following":
                userID = getIntent().getStringExtra("userID");
                getFollowingID();
                listTitleTextView.setText("Followings");
                break;
            case "Followers":
                userID = getIntent().getStringExtra("userID");
                getFollowersID();
                listTitleTextView.setText("Followers");
                break;
            case "Likes":
                postID = getIntent().getStringExtra("postID");
                getLikesUserID();
                listTitleTextView.setText("Likes");
                break;
        }
    }

    /**
     * Get all IDs of users that likes a specific post.
     */
    private void getLikesUserID() {
        DatabaseReference ref = databaseRef.child(databaseLikes).child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    userIDList.add(subSnapshot.getKey());
                }

                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all userID from likes", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Get all IDs of users that following @userID
     */
    private void getFollowersID() {
        DatabaseReference ref = databaseRef.child(databaseFollowedby).child(userID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    userIDList.add(subSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all followers", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Get all IDs of users that followed by @userID
     */
    private void getFollowingID() {
        DatabaseReference ref = databaseRef.child(databaseFollowing).child(userID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    userIDList.add(subSnapshot.getKey());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all followings", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * From IDs @userIDList, save the corresponding user in a list @mUserList
     */
    private void getUsers() {
        DatabaseReference ref = databaseRef.child(databaseUsers);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    User user = subSnapshot.getValue(User.class);

                    assert user != null;
                    if (userIDList.contains(user.getUserID())) {
                        mUserList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all users", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    public void closeImageViewOnClick(View view) {
        finish();
    }
}