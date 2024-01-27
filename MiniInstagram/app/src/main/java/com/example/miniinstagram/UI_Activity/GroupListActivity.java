package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miniinstagram.Adapter.GroupAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Group;
import com.example.miniinstagram.model.GroupAdapterCode;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListActivity extends AppCompatActivity {

    public ImageView closeImageView;
    public RecyclerView recyclerView;
    public EditText newGroupEditText;
    public ImageView newGroupImageView;
    private List<Group> groupList;
    private GroupAdapter groupAdapter;
    private FirebaseUser fbUser;
    private DatabaseReference databaseReference;
    private ValueEventListener getGroupsListener;
    private String databaseGroups = "Groups";
    private String databaseUserGroups = "User-groups";
    private String TAG = "Group list activity: ";
    private String userID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Intent intent = getIntent();
        if (intent.hasExtra("userID")) {
            userID = intent.getStringExtra("userID");
        }

        closeImageView = findViewById(R.id.closeImageView);
        recyclerView = findViewById(R.id.recycler_view);
        newGroupEditText = findViewById(R.id.newGroupEditText);
        newGroupImageView = findViewById(R.id.newGroupImageView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerGroup = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManagerGroup);
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupList, GroupAdapterCode.GROUP_ADAPTER_CODE_GENERAL, userID);
        recyclerView.setAdapter(groupAdapter);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        addNewGroup();
        getAllGroups();
    }

    public void closeImageViewOnClick(View view) {
        finish();
    }

    private void getAllGroups() {
        DatabaseReference ref = databaseReference.child(databaseUserGroups).child(fbUser.getUid());

        getGroupsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    Group currGroup = subSnapshot.getValue(Group.class);
                    groupList.add(currGroup);
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Failed to get all groups", error.toException());
            }
        };

        ref.addValueEventListener(getGroupsListener);
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
                    Toast.makeText(GroupListActivity.this,"Please add a group name", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(GroupListActivity.this,
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

    public void onStop() {
        super.onStop();

        if (getGroupsListener != null) {
            databaseReference.child(databaseUserGroups)
                             .child(fbUser.getUid())
                             .removeEventListener(getGroupsListener);
        }
    }
}