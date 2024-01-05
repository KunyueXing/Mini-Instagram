package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addCommentEditText;
    private CircleImageView profileImageImageView;
    private TextView postTextView;

    private FirebaseUser fbUser;
    private DatabaseReference databaseReference;

    private String postID;
    private String authorID;
    private String TAG = "CommentActivity: ";
    private String databaseUsers = "Users";
    private String databasePostComments = "Post-comments";
    private String databaseComments = "Comments";

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
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getUserProfileImage();
    }

    /**
     * Show user profile image
     */
    private void getUserProfileImage() {
        DatabaseReference ref = databaseReference.child(databaseUsers).child(fbUser.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getProfilePicUriStr().equals("default")) {
                    profileImageImageView.setImageResource(R.drawable.default_avatar);
                } else {
                    Picasso.get()
                           .load(user.getProfilePicUriStr())
                           .placeholder(R.drawable.default_avatar)
                           .into(profileImageImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: Can't access User profile", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }
}