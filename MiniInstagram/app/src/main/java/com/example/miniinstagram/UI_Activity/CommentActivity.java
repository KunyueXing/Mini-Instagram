package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Comment;
import com.example.miniinstagram.model.User;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addCommentEditText;
    private CircleImageView profileImageImageView;
    private TextView postTextView;
    private ImageView goBackImageView;

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

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        authorID = intent.getStringExtra("authorID");

        addCommentEditText = findViewById(R.id.add_comment);
        profileImageImageView = findViewById(R.id.profile_image);
        postTextView = findViewById(R.id.post);
        goBackImageView = findViewById(R.id.goBackImageView);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

//        Toast.makeText(CommentActivity.this,"Welcome to Comments page", Toast.LENGTH_SHORT).show();

        getUserProfileImage();
    }

    /**
     * When click on goBack imageview, go back
     * @param view
     */
    public void goBackImageViewOnclick(View view) {
        finish();
    }

    /**
     * When user click the post textview, upload the comment to database
     * @param view
     */
    public void postCommentOnClick(View view) {
        if (TextUtils.isEmpty(addCommentEditText.getText().toString())) {
            Toast.makeText(CommentActivity.this,
                              "Please add comment",
                                   Toast.LENGTH_SHORT).show();
        } else {
            uploadComment();
        }
    }

    /**
     * Upload Comment to database
     */
    private void uploadComment() {
        String commentID = databaseReference.child(databaseComments).push().getKey();

        Comment currComment = new Comment(commentID, authorID, addCommentEditText.getText().toString());
        Map<String, Object> commentValues = currComment.toMap();
        Map<String, Object> childUpdates = new HashMap<String, Object>();

        childUpdates.put("/" + databaseComments + "/" + commentID, commentValues);
        childUpdates.put("/" + databasePostComments + "/" + postID + "/" + commentID, commentValues);

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this,
                            "Post comment success", Toast.LENGTH_SHORT).show();

                    return;
                }

                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: update realtime databsae error-" + e.getMessage().toString());
            }
        };

        databaseReference.updateChildren(childUpdates)
                         .addOnCompleteListener(listener);

        addCommentEditText.setText("");
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