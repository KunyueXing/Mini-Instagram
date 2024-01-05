package com.example.miniinstagram.UI_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addCommentEditText;
    private CircleImageView profileImageImageView;
    private TextView postTextView;

    private FirebaseUser fbUser;

    private String postID;
    private String authorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        /**
         * Set whether home should be displayed as an "up" affordance. Set this to true if
         * selecting "home" returns up by a single level in your UI rather than back to the top
         * level or front page.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        authorID = intent.getStringExtra("authorID");

        addCommentEditText = findViewById(R.id.add_comment);
        profileImageImageView = findViewById(R.id.profile_image);
        postTextView = findViewById(R.id.post);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}