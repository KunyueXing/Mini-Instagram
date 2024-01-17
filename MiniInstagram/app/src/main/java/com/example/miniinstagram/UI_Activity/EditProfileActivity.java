package com.example.miniinstagram.UI_Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.example.miniinstagram.fragments.ProfileFragment;
import com.example.miniinstagram.model.Profile;
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

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView closeImageView;
    private TextView saveTextView;
    private TextView updateAvatarTextView;
    private ImageView profileImageImageView;

    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText bioEditText;
    private ActivityResultLauncher<String> choosePhoto;
    private Uri imageUri;

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

        /*
         * Set up a callback when a user have selected an image.
         */
        ActivityResultCallback<Uri> choosePhotoCallback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                displayPhoto(result);
            }
        };
        choosePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                choosePhotoCallback);

        showUserInfo();
    }

    public void addPhotoOnClick(View view) {
        choosePhoto.launch("image/*");
    }

    /*
     * Once an image has been selected, display it on addImageView and save the URI in imageUri,
     * which will be used later when we upload the image.
     */
    private void displayPhoto(Uri result) {
        Log.d(TAG, "KX: " + result);
        imageUri = result;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            profileImageImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When open the edit profile page, show user basic info accordingly.
     */
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

    public void closeOnClick(View view) {
        finish();

//        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
    }
}