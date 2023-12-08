package com.example.miniinstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miniinstagram.Adapter.UserAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;
    private SocialAutoCompleteTextView searchBarTextView;
    private String TAG = "Search fragment: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        searchBarTextView = view.findViewById(R.id.search_bar);

        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        getAllUsers();

        return view;
    }

    /**
     * When user doesn't type anything in the search bar, will show all users as default.
     */
    private void getAllUsers() {
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("Users");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if user didn't type anything in search bar
                if (TextUtils.isEmpty(searchBarTextView.getText().toString())) {
                    mUsers.clear();

                    /**
                     * getValue() method is used to marshall the data contained in this snapshot
                     * into a class of your choosing. The class must fit 2 simple constraints:
                     *
                     * 1. The class must have a default constructor that takes no arguments
                     * 2. The class must define public getters for the properties to be assigned.
                     * Properties without a public getter will be set to their default value when
                     * an instance is deserialized
                     */
                    for (DataSnapshot subSnapShot : snapshot.getChildren()) {
                        User user = subSnapShot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();

                    userDatabaseRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value when get all users from Users in DB", error.toException());
                userDatabaseRef.removeEventListener(this);
            }
        };

        userDatabaseRef.addValueEventListener(listener);
    }
}