package com.example.miniinstagram.UI_Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private String imageUrlFromStorage;
    private StorageTask uploadImageTask;
    private ProgressBar progressBar;

    private FirebaseUser fbUser;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private User currUser;
    private String databaseUsers = "Users";
    private String storageProfileImage = "Profile_Image";
    private String TAG = "Edit Profile Activity: ";

    @SuppressLint("MissingInflatedId")
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
        progressBar = findViewById(R.id.progressBar);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference(storageProfileImage);
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

    /**
     * When click on the avatar image, go to choose photo from library.
     * @param view
     */
    public void addPhotoOnClick(View view) {
        choosePhoto.launch("image/*");
    }

    /**
     * When click on the text "Update avatar", upload the chose photo to Firebase Storage.
     * @param view
     */
    public void updateAvatarOnClick(View view) {
        uploadImageToStorage();
    }

    /**
     * When click on the save textview, upload the edited information to the database.
     * @param view
     */
    public void saveOnClick(View view) {
        uploadProfileToDatabase();
    }

    /*
     * Upload the image to Firebase Storage. If it succeeds, download the URL of this image.
     */
    private void uploadImageToStorage() {
        // If there is no image selected, just return.
        if (imageUri == null) {
            Toast.makeText(EditProfileActivity.this,
                    "No image selected", Toast.LENGTH_LONG).show();
            return;
        }

//        setEditable(false);
        progressBar.setVisibility(View.VISIBLE);

        // get the extension of image, e.g., jpg
        String imageExt = getImageExtension(imageUri);
        final StorageReference imageRef =
                storageRef.child(System.currentTimeMillis() + "." + imageExt);

        uploadImageTask = imageRef.putFile(imageUri);

        OnCompleteListener listener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditProfileActivity.this,
                            "Update avatar success!",
                            Toast.LENGTH_SHORT).show();

                    downloadImageUrlFromStorage(imageRef);
                    return;
                }

                StorageException e = (StorageException) task.getException();
                progressBar.setVisibility(View.INVISIBLE);
//                setEditable(true);
                Toast.makeText(EditProfileActivity.this,
                        "Can't upload image to cloud storage", Toast.LENGTH_LONG).show();
                Log.e(TAG, "KX: upload " + String.valueOf(e.getErrorCode()));
            }
        };

        uploadImageTask.addOnCompleteListener(listener);
    }

    /*
     * Download the URL of previously uploaded image. If it succeeds, we'll go ahead and upload
     * user's post.
     */
    private void downloadImageUrlFromStorage(final StorageReference imageRef) {
//        setEditable(false);

        OnCompleteListener listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
//                    Log.d(TAG, "KX: Download image from storage successful");

                    imageUrlFromStorage = task.getResult().toString();
                    return;
                }

                StorageException e = (StorageException) task.getException();
                progressBar.setVisibility(View.INVISIBLE);
//                setEditable(true);
                Toast.makeText(EditProfileActivity.this,
                        "Can't Download image from cloud storage", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "KX: download " + String.valueOf(e.getErrorCode()));
            }
        };

        imageRef.getDownloadUrl()
                .addOnCompleteListener(listener);
    }

    /*
     * Upload user's info to database.
     */
    private void uploadProfileToDatabase() {
//        Log.d(TAG, "KX: Begin to update database");

//        setEditable(false);
        progressBar.setVisibility(View.VISIBLE);

        // Let user know that she needs to click @updateAvatar textview to save the chosen photo
        if (imageUri != null && imageUrlFromStorage == null) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(EditProfileActivity.this,
                    "Please update avatar before save!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUrlFromStorage != null) {
            currUser.setProfilePicUriStr(imageUrlFromStorage);
        }

        String username = usernameEditText.getText().toString();
        if (username.length() != 0 && !username.equals(currUser.getUsername()) && isUsernameValid(username)) {
            currUser.setUsername(username);
        }

        String name = nameEditText.getText().toString();
        currUser.setName(name);
        String bio = bioEditText.getText().toString();
        currUser.setBio(bio);

        Map<String, Object> childUpdates = new HashMap<>();
        // User info is saved under /Users/, {key: userID, value: userInfo}
        childUpdates.put("/Users/" + fbUser.getUid(), currUser.toMap());

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditProfileActivity.this,
                            "Edit profile success",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EditProfileActivity.this,
                        "Error occur when accessing database",
                        Toast.LENGTH_SHORT).show();
            }
        };

        databaseRef.updateChildren(childUpdates)
                   .addOnCompleteListener(listener);
    }

    /**
     * Check if the username input is valid. The username must be unique.
     * @param usernameStr
     * @return
     */
    private boolean isUsernameValid(String usernameStr) {
        final boolean[] result = {true};

        DatabaseReference ref = databaseRef.child(databaseUsers);
        // Query Users table with the username provided.
        Query query = ref.orderByChild("username").equalTo(usernameStr);

        // Define an event listener associated with the query.
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If we find a user with the same username, display an error message and return.
                    progressBar.setVisibility(View.INVISIBLE);
                    usernameEditText.setError("Username already exists");
                    usernameEditText.requestFocus();

                    result[0] = false;
                    return;
                }

                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read all usernames.", error.toException());
                query.removeEventListener(this);
            }
        };

        query.addValueEventListener(eventListener);

        return result[0];
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

    // Get the extension of a file, e.g., jpg, png, etc.
    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        Log.d(TAG, "KX: get extension " + extension);
        return extension;
    }

    /**
     * When open the edit profile page, show user basic info accordingly.
     */
    private void showUserInfo() {
        DatabaseReference ref = databaseRef.child(databaseUsers).child(fbUser.getUid());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currUser = snapshot.getValue(User.class);
                nameEditText.setText(currUser.getName());
                usernameEditText.setText(currUser.getUsername());
                bioEditText.setText(currUser.getBio());

                Picasso.get()
                       .load(currUser.getProfilePicUriStr())
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