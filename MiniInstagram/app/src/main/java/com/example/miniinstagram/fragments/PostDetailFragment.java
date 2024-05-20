package com.example.miniinstagram.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miniinstagram.Adapter.PostAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    private String postID;
    private RecyclerView recyclerView;
    private ImageView goBackImageView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private DatabaseReference databaseReference;
    private String databasePosts = "Posts";
    private String TAG = "post detail fragment: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences sharedPref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postID = sharedPref.getString("postID", "none");

        goBackImageView = view.findViewById(R.id.goBackImageView);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , postList);
        recyclerView.setAdapter(postAdapter);
//        Toast.makeText(getContext(), "post detail", Toast.LENGTH_SHORT).show();

        getPosts();
        goBack();

        return view;
    }

    /**
     * Go back to the profile page.
     */
    public void goBack() {
        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((FragmentActivity)getContext()).getSupportFragmentManager();
                // Sets whether or not to allow optimizing operations within and across
                // transactions. This will remove redundant operations, eliminating operations
                // that cancel.
                fragmentManager.beginTransaction()
                               .replace(R.id.fragment_container, ProfileFragment.class, null)
                               .setReorderingAllowed(true).commit();
            }
        });
    }

    /**
     * Get post content from the database.
     */
    private void getPosts() {
        DatabaseReference ref = databaseReference.child(databasePosts).child(postID);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                Post post = snapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get the post", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }
}