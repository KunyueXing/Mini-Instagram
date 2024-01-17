package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView closeImageView;
    private TextView saveTextView;
    private TextView updateAvatarTextView;
    private ImageView profileImageImageView;

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText bioEditText;

    private FirebaseUser fbUser;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private String databaseUsers = "Users";
    private String TAG = "Edit Profile Activity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        closeImageView = findViewById(R.id.closeImageView);
        profileImageImageView = findViewById(R.id.profileImageImageView);
        saveTextView = findViewById(R.id.saveTextView);
        updateAvatarTextView = findViewById(R.id.avatarUpdateTextView);
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        bioEditText = findViewById(R.id.bioEditText);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        showUserInfo();
    }

    private void showUserInfo() {
        DatabaseReference ref = databaseRef.child(databaseUsers).child(fbUser.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                nameEditText.setText(user.getName());
                usernameEditText.setText(user.getUsername());
                bioEditText.setText(user.getBio());

                Picasso.get()
                       .load(user.getProfilePicUriStr())
                       .placeholder(R.drawable.default_avatar)
                       .into(profileImageImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Can't get the info of the user", error.toException());
            }
        };

        ref.addListenerForSingleValueEvent(listener);
    }
}