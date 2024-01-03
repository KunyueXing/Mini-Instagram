package com.example.miniinstagram.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.miniinstagram.Adapter.PostAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> allPostList;
    private List<String> allFollowingList;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String databaseFollowing = "User-following";
    private String databaseUserPosts = "User-Posts";
    private String TAG = "HomeFragment: ";

    private ValueEventListener allFollowingListener;
    private ValueEventListener allFollowingPostsListener;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = view.findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // Latest post will be on the top
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        allPostList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), allPostList);
        recyclerView.setAdapter(postAdapter);

        allFollowingList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_circular);

        getAllFollowing();

        return view;
    }

    /**
     * Get all users followed by the current user and store their userID in a list.
     */
    private void getAllFollowing() {
        DatabaseReference ref = databaseReference.child(databaseFollowing)
                                                 .child(firebaseUser.getUid());

        allFollowingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFollowingList.clear();

                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    allFollowingList.add(subSnapshot.getKey());

//                    Toast.makeText(getContext(), subSnapshot.getKey(),
//                            Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(getContext(), "Get all followings",
//                        Toast.LENGTH_SHORT).show();
                getAllFollowingPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Get all following:onCancelled", error.toException());
            }
        };

        ref.addValueEventListener(allFollowingListener);
    }

    /**
     * Get all posts from all user followings and store them in postList.
     */
    private void getAllFollowingPosts() {
        DatabaseReference ref = databaseReference.child(databaseUserPosts);

        allFollowingPostsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allPostList.clear();

                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    for (String followingID : allFollowingList) {
                        if (!subSnapshot.getKey().equals(followingID)) {
                            continue;
                        }

                        for (DataSnapshot postSnapshot : subSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);

                            allPostList.add(post);
                        }
                    }
                }

//                Toast.makeText(getContext(), "Get all following posts",
//                        Toast.LENGTH_SHORT).show();

                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Get all posts from all following:onCancelled", error.toException());
            }
        };

        ref.addValueEventListener(allFollowingPostsListener);
    }

    @Override
    /**
     * When the fragment is stopped, cleaning up all listeners accordingly
     */
    public void onStop() {
        super.onStop();

        if (allFollowingListener != null) {
            databaseReference.child(databaseFollowing)
                             .child(firebaseUser.getUid())
                             .removeEventListener(allFollowingListener);
        }

        if (allFollowingPostsListener != null) {
            databaseReference.child(databaseUserPosts)
                             .removeEventListener(allFollowingPostsListener);
        }
    }
}