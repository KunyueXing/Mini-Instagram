package com.example.miniinstagram.UI_Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private ImageView closeImageView;
    private ImageView addImageView;
    private TextView postTextView;
    private EditText postContentEditText;
    private ProgressBar progressBar;

    private StorageReference storageReference;
    private StorageTask uploadImageTask;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Uri imageUri;
    private ActivityResultLauncher<String> choosePhoto;

    private String TAG = "new post activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        closeImageView = findViewById(R.id.closeImageView);
        addImageView = findViewById(R.id.addImageImageView);
        postTextView = findViewById(R.id.postTextView);
        postContentEditText = findViewById(R.id.postContentEditText);
        progressBar = findViewById(R.id.progressBar);

        // Images are stored in "Posts_Image" on Firebase Storage.
        storageReference = FirebaseStorage.getInstance().getReference("Posts_Image");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Log.e(TAG, "KX: can't get current user");
            goToHomepage();
        }

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
    }

    public void postTextViewOnClick(View view) {
        uploadImageToStorage();
    }

    public void addImageViewOnClick(View view) {
        choosePhoto.launch("image/*");
    }

    public void closeImageViewOnClick(View view) {
        goToHomepage();
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
            addImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Upload the image to Firebase Storage. If it succeeds, download the URL of this image.
     */
    private void uploadImageToStorage() {
        // If there is no image selected, just return.
        if (imageUri == null) {
            Toast.makeText(NewPostActivity.this,
                    "No image selected", Toast.LENGTH_LONG).show();
            return;
        }

        setEditable(false);
        progressBar.setVisibility(View.VISIBLE);

        // get the extension of image, e.g., jpg
        String imageExt = getImageExtension(imageUri);
        final StorageReference imageRef =
                storageReference.child(System.currentTimeMillis() + "." + imageExt);

        uploadImageTask = imageRef.putFile(imageUri);

        OnCompleteListener listener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(NewPostActivity.this,
//                            "Upload image to cloud storage successful",
//                            Toast.LENGTH_LONG).show();

                    downloadImageUrlFromStorage(imageRef);
                    return;
                }

                StorageException e = (StorageException) task.getException();
                progressBar.setVisibility(View.INVISIBLE);
                setEditable(true);
                Toast.makeText(NewPostActivity.this,
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
//                    Toast.makeText(NewPostActivity.this,
//                            "Download image from cloud storage successful",
//                            Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "KX: Download image from storage successful");

                    uploadPostToDatabase(task);
                    return;
                }

                StorageException e = (StorageException) task.getException();
                progressBar.setVisibility(View.INVISIBLE);
                setEditable(true);
                Toast.makeText(NewPostActivity.this,
                        "Can't Download image from cloud storage", Toast.LENGTH_LONG).show();
                Log.e(TAG, "KX: download " + String.valueOf(e.getErrorCode()));
            }
        };

        imageRef.getDownloadUrl()
                .addOnCompleteListener(listener);
    }

    /*
     * Upload user's post to database.
     */
    private void uploadPostToDatabase(Task task) {
//        Log.d(TAG, "KX: Begin to update database");

        String imageUrl = task.getResult().toString();
        String postID = databaseReference.child("Posts").push().getKey();
        String authorID = user.getUid();
        String postContent = postContentEditText.getText().toString();
        Post post = new Post(postID, postContent, imageUrl, authorID);
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

//        Log.d(TAG, "KX: Download image " + imageUrl);
//        setEditable(false);
//        Log.d(TAG, "KX: postID " + postID);
//        Log.d(TAG, "KX: authorId " + authorID);
//        Log.d(TAG, "KX: content" + postContent);

        // Insert the post to 2 tables, one indexed by postID and another one indexed by authorID.
        childUpdates.put("/Posts/" + postID, postValues);
        childUpdates.put("/User-Posts/" + authorID + "/" + postID, postValues);

        OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewPostActivity.this,
                            "Post success", Toast.LENGTH_LONG).show();

                    goToHomepage();
                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);
                setEditable(true);
                DatabaseException e = (DatabaseException) task.getException();
                Log.e(TAG, "KX: update realtime databsae error-" + e.getMessage().toString());
            }
        };

        databaseReference.updateChildren(childUpdates)
                         .addOnCompleteListener(listener);
    }

    // Back to homepage
    private void goToHomepage() {
        startActivity(new Intent(NewPostActivity.this , HomepageActivity.class));
        finish();
    }

    // Get the extension of a file, e.g., jpg, png, etc.
    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        Log.d(TAG, "KX: get extension " + extension);
        return extension;
    }

    // When isEditable is false, user can't edit in this page.
    private void setEditable(boolean isEditable) {
        // when isEditable is false, close view can't be selected and focused, and can't be edited.
        closeImageView.setFocusable(isEditable);
        // user touches widget on phone with touch screen
        closeImageView.setFocusableInTouchMode(isEditable);
        closeImageView.setClickable(isEditable);

        addImageView.setFocusable(isEditable);
        addImageView.setClickable(isEditable);
        addImageView.setFocusableInTouchMode(isEditable);

        postTextView.setFocusable(isEditable);
        postTextView.setClickable(isEditable);
        postTextView.setFocusableInTouchMode(isEditable);

        postContentEditText.setFocusable(isEditable);
        postContentEditText.setClickable(isEditable);
        postContentEditText.setFocusableInTouchMode(isEditable);
    }
}