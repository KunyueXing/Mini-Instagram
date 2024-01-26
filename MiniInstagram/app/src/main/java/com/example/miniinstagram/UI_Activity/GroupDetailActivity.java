package com.example.miniinstagram.UI_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {
    public TextView groupNameTextView;
    public TextView groupMemberNumTextView;
    public Button editGroupButton;
    public TextView descriptionTextView;
    public RecyclerView recyclerView;
    public ImageView closeImageView;

    private List<User> mUsers;
    private String groupID;
    private FirebaseUser fbUser;
    private DatabaseReference databaseReference;
    private String TAG = "Group Detail Activity: ";
    private String databaseUsers = "Users";

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

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}