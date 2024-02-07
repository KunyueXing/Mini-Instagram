package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.Adapter.GroupAdapter;
import com.example.miniinstagram.Adapter.PostAdapter;
import com.example.miniinstagram.Adapter.UserAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.GroupAdapterCode;
import com.example.miniinstagram.model.Post;
import com.example.miniinstagram.model.User;
import com.example.miniinstagram.model.UserAdapterCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupDetailActivity extends AppCompatActivity {
    public TextView groupNameTextView;
    public TextView groupMemberNumTextView;
    public Button editGroupButton;
    public TextView descriptionTextView;
    public ImageButton groupPostsImageButton;
    public ImageButton groupMemberImageButton;
    public RecyclerView recycler_view_posts;
    public RecyclerView recyclerView;
    public ImageView closeImageView;
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;

    private List<User> mUsers;
    private List<Post> mPosts;
    private String groupID;
    private FirebaseUser fbUser;
    private ValueEventListener getGroupMembersIDListener;

    private ValueEventListener getGroupInfoListener;
    private DatabaseReference databaseReference;
    private String TAG = "Group Detail Activity: ";
    private String databaseUsers = "Users";
    private String databaseUserPosts = "User-Posts";
    private String databaseGroups = "Groups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        groupMemberNumTextView = findViewById(R.id.groupMemberNumTextView);
        editGroupButton = findViewById(R.id.editGroupButton);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        recyclerView = findViewById(R.id.recycler_view);
        closeImageView = findViewById(R.id.closeImageView);
        groupMemberImageButton = findViewById(R.id.groupMemberImageButton);
        groupPostsImageButton = findViewById(R.id.groupPostsImageButton);
        recycler_view_posts = findViewById(R.id.recycler_view_posts);

        recycler_view_posts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerPost = new LinearLayoutManager(this);
        // Latest post will be on the top
        linearLayoutManagerPost.setStackFromEnd(true);
        linearLayoutManagerPost.setReverseLayout(true);

        recycler_view_posts.setLayoutManager(linearLayoutManagerPost);
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(this, mPosts);
        recycler_view_posts.setAdapter(postAdapter);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerGroup = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManagerGroup);
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, UserAdapterCode.USER_ADAPTER_CODE_GENERAL);
        recyclerView.setAdapter(userAdapter);

        enableGroupMembers(false);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");

        getGroupInfo();
        getUserList();

        getGroupPosts();

        showGroupMembers();
        showGroupPosts();
    }

    /**
     * Get all posts that are posted by the group members.
     */
    private void getGroupPosts() {

        Set<String> userIDList  = getUserIDs();
        if (userIDList == null) {
            return;
        }

        DatabaseReference ref = databaseReference.child(databaseUserPosts);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPosts.clear();

                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    for (String followingID : userIDList) {
                        if (!subSnapshot.getKey().equals(followingID)) {
                            continue;
                        }

                        for (DataSnapshot postSnapshot : subSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);

                            mPosts.add(post);
                        }
                    }
                }

                // sort the posts according to its creation time.
                Collections.sort(mPosts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Get all posts from all following:onCancelled", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * When click on the groupMember ImageButton, show group member user list
     */
    private void showGroupMembers() {
        groupMemberImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableGroupMembers(true);
            }
        });
    }

    /**
     * When click on the groupPosts ImageButton, show group posts list
     */
    private void showGroupPosts() {
        groupPostsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableGroupMembers(false);
            }
        });
    }

    /**
     * Show either group members user list or group posts list
     * @param isEnabled
     */
    private void enableGroupMembers(boolean isEnabled) {
        if (isEnabled) {
            recycler_view_posts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            recycler_view_posts.setVisibility(View.VISIBLE);
        }
    }

    /**
     * According to userID, find the user (in type User) and saved it in @mUsers list
     */
    private void getUserList() {
        Set<String> userIDList  = getUserIDs();
        if (userIDList == null) {
            return;
        }
        DatabaseReference ref = databaseReference.child(databaseUsers);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    User user = subSnapshot.getValue(User.class);

                    assert user != null;
                    if (userIDList.contains(user.getUserID())) {
                        mUsers.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed get users from User database", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * Get all users that are stored in this group (group members). The userID is saved and returned
     * in a set.
     * @return
     */
    private Set<String> getUserIDs() {
        Set<String> result = new HashSet<>();

        DatabaseReference ref = databaseReference.child(databaseGroups).child(groupID);

        getGroupMembersIDListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                for (String userID : group.getMembers().keySet()) {
                    result.add(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed get user ids from the group", error.toException());
            }
        };

        ref.addValueEventListener(getGroupMembersIDListener);
        return result;
    }

    /**
     * Get group name, number of members, description and show them on the page.
     */
    private void getGroupInfo() {
        DatabaseReference ref = databaseReference.child(databaseGroups).child(groupID);

        getGroupInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                groupNameTextView.setText(group.getGroupName());
                int groupMemberNumber = 0;
                if (group.getMembers() != null) {
                    groupMemberNumber = group.getMembers().size();
                }
                groupMemberNumTextView.setText(groupMemberNumber + " members in this group");

                if (group.getDescription() != null) {
                    descriptionTextView.setText(group.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get group info", error.toException());
            }
        };

        ref.addValueEventListener(getGroupInfoListener);
    }

    public void closeOnClick(View view) {
        finish();
    }

    @Override
    public void onStop() {

        super.onStop();

        if (getGroupInfoListener != null) {
            databaseReference.child(databaseGroups)
                             .child(groupID)
                             .removeEventListener(getGroupInfoListener);
        }

        if (getGroupMembersIDListener != null) {
            databaseReference.child(databaseGroups)
                             .child(groupID)
                             .removeEventListener(getGroupMembersIDListener);
        }
    }
}