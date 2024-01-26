package com.example.miniinstagram.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.Adapter.GroupAdapter;
import com.example.miniinstagram.Adapter.PostGridAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.UI_Activity.EditProfileActivity;
import com.example.miniinstagram.UI_Activity.OptionsActivity;
import com.example.miniinstagram.UI_Activity.UserListActivity;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.Notification;
import com.example.miniinstagram.model.NotificationType;
import com.example.miniinstagram.model.User;
import com.example.miniinstagram.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private CircleImageView profileImageCircleImageView;
    private ImageView optionsImageView;
    private ImageButton myPostsImageButton;
    private TextView postsTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private TextView nameTextView;
    private TextView bioTextView;
    private TextView usernameTextView;
    private Button editProfileButton;
    private RelativeLayout groupBarLayout;
    private RecyclerView recycler_view_group;
    private List<Group> groupList;
    private GroupAdapter groupAdapter;
    private ImageButton myGroupImageButton;
    private EditText newGroupEditText;
    private ImageView newGroupImageView;
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
    private ValueEventListener getUserInfoListener;
    private ValueEventListener postsNumListener;

    private String TAG = "Profile fragment: ";
    private String storagePostsImage = "Posts_Image";
    private String databaseUserPosts = "User-Posts";
    private String databaseUsers = "Users";
    private String databaseFollowing = "User-following";
    private String databaseNotifications = "Notifications";
    private String databaseFollowedby = "User-followedby";
    private String databaseGroups = "Groups";
    private String databaseUserGroups = "User-groups";

    @SuppressLint("MissingInflatedId")
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
        myPostsImageButton = view.findViewById(R.id.my_posts);

        postsTextView = view.findViewById(R.id.posts);
        followersTextView = view.findViewById(R.id.followers);
        followingTextView = view.findViewById(R.id.following);
        nameTextView = view.findViewById(R.id.name);
        bioTextView = view.findViewById(R.id.bio);
        usernameTextView = view.findViewById(R.id.username);
        editProfileButton = view.findViewById(R.id.edit_profile);

        groupBarLayout = view.findViewById(R.id.group_bar);
        recycler_view_group = view.findViewById(R.id.recycler_view_group);
        myGroupImageButton = view.findViewById(R.id.myGroupImageButton);
        newGroupEditText = view.findViewById(R.id.newGroupEditText);
        newGroupImageView = view.findViewById(R.id.newGroupImageView);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext() , 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postGridList = new ArrayList<>();
        postGridAdapter = new PostGridAdapter(getContext() , postGridList);
        recyclerView.setAdapter(postGridAdapter);

        recycler_view_group.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerGroup = new LinearLayoutManager(getContext());
        recycler_view_group.setLayoutManager(linearLayoutManagerGroup);
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(), groupList);
        recycler_view_group.setAdapter(groupAdapter);

        enableViewGroup(false);

        if (profileUserID.equals(fbUser.getUid())) {
            editProfileButton.setText("Edit profile");
            myGroupImageButton.setVisibility(View.VISIBLE);
        } else {
            checkFollowingStatus();
        }

        showGroupContent();
        showPostsContent();

        showUserBasicContent();
        editProfileOrFollow();
        getPostsList();

        getFollowingUsers();
        getFollowers();
        gotoOptions();

        addNewGroup();

        return view;
    }

    /**
     * When click on the new group imageview, check if the group name is valid and then
     * upload to database.
     */
    public void addNewGroup() {
        newGroupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(newGroupEditText.getText().toString())) {
                    Toast.makeText(getContext(),"Please add a group name", Toast.LENGTH_SHORT).show();
                } else {
                    uploadGroup();
                }
            }
        });
    }

    /**
     * Upload the group content to database.
     */
    private void uploadGroup() {
        String groupID = databaseReference.child(databaseGroups).push().getKey();

        Group group = new Group(groupID, newGroupEditText.getText().toString(), fbUser.getUid());
        Map<String, Object> groupValues = group.toMap();
        Map<String, Object> childUpdates = new HashMap<String, Object>();

        childUpdates.put("/" + databaseGroups + "/" + groupID, groupValues);
        childUpdates.put("/" + databaseUserGroups + "/" + fbUser.getUid() + "/" + groupID, groupValues);

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Create new group success", Toast.LENGTH_SHORT).show();

                    return;
                }

                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: failed creating new group" + e.getMessage().toString());
            }
        };

        databaseReference.updateChildren(childUpdates)
                         .addOnCompleteListener(listener);

        newGroupEditText.setText("");
    }

    /**
     * When click on the @my post imagebutton, disable view groups. Show user posts in grid.
     */
    private void showPostsContent() {
        myPostsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableViewGroup(false);
            }
        });
    }

    private void showGroupContent() {
        myGroupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableViewGroup(true);
            }
        });
    }

    private void enableViewGroup(boolean isEnabled) {
        if (isEnabled) {
            groupBarLayout.setVisibility(View.VISIBLE);
            recycler_view_group.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);
        } else {
            groupBarLayout.setVisibility(View.GONE);
            recycler_view_group.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void gotoOptions() {
        optionsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , OptionsActivity.class);
                startActivity(intent);
            }
        });
    }

    // When click on the number of followings, jump to user list page which listed all following users
    private void getFollowingUsers() {
        followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                intent.putExtra("userID", profileUserID);
                intent.putExtra("userListTitle", "Following");
                startActivity(intent);
            }
        });
    }

    // When click on the number of followers, jump to user list page which listed all followers
    private void getFollowers() {
        followersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserListActivity.class);
                intent.putExtra("userID", profileUserID);
                intent.putExtra("userListTitle", "Followers");
                startActivity(intent);
            }
        });
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

                // Sort the list in Chronologically order so that the latest comes first.
                Collections.reverse(postGridList);
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
        Map<String, Object> childUpdates = new HashMap<String, Object>();
        String path1 = "/" + databaseFollowing + "/" + fbUser.getUid() + "/" +profileUserID;
        String path2 = "/" + databaseFollowedby + "/" + profileUserID + "/" + fbUser.getUid();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText = editProfileButton.getText().toString();

                if (btnText.equals("Edit profile")) {
                    // go to edit profile activity
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btnText.equals("Follow")) {
                    childUpdates.put(path1, true);
                    childUpdates.put(path2, true);

                    OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                return;
                            }

                            DatabaseException e = (DatabaseException) task.getException();
                            Log.e(TAG, "KX: failed following user" + e.getMessage().toString());
                        }
                    };

                    databaseReference.updateChildren(childUpdates)
                                     .addOnCompleteListener(listener);

                    editProfileButton.setText("Following");
                } else if (btnText.equals("Following")) {
                    childUpdates.put(path1, null);
                    childUpdates.put(path2, null);

                    OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendNotifications();
                                return;
                            }

                            DatabaseException e = (DatabaseException) task.getException();
                            Log.e(TAG, "KX: failed unfollowing user" + e.getMessage().toString());
                        }
                    };

                    databaseReference.updateChildren(childUpdates)
                                     .addOnCompleteListener(listener);
                    editProfileButton.setText("Follow");
                }
            }
        });
    }

    private void sendNotifications() {
        DatabaseReference ref = databaseReference.child(databaseNotifications).child(profileUserID);
        String notificationID = ref.push().getKey();

        Notification notification = new Notification(notificationID, fbUser.getUid(), NotificationType.NOTIFICATION_TYPE_FOLLOWERS);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(notificationID, notification.toMap());

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w(TAG, "onComplete: upload notification success");
                    return;
                }

                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: can't upload notifications" + e.getMessage().toString());
            }
        };

        ref.updateChildren(childUpdates).addOnCompleteListener(listener);
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

        getUserInfoListener = new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value when get user info from users.", error.toException());
            }
        };

        usersRef.addValueEventListener(getUserInfoListener);
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

        if (getUserInfoListener != null) {
            databaseReference.child(databaseUsers)
                             .child(profileUserID)
                             .removeEventListener(getUserInfoListener);
        }

        transferredID.edit().clear().commit();
    }
}