package com.example.miniinstagram.UI_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.miniinstagram.Adapter.GroupAdapter;
import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    public ImageView closeImageView;
    public RecyclerView recyclerView;
    public EditText newGroupEditText;
    public ImageView newGroupImageView;
    private List<Group> groupList;
    private GroupAdapter groupAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        closeImageView = findViewById(R.id.closeImageView);
        recyclerView = findViewById(R.id.recycler_view);
        newGroupEditText = findViewById(R.id.newGroupEditText);
        newGroupImageView = findViewById(R.id.newGroupImageView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerGroup = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManagerGroup);
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupList);
        recyclerView.setAdapter(groupAdapter);
    }
}