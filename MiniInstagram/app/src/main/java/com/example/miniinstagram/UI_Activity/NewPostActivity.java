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

        // Images will be stored in Firebase Storage, under "Posts_Image"
        storageReference = FirebaseStorage.getInstance().getReference("Posts_Image");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        /*
         * set an activity of choosing a photo from device, saving result in variable imageUri and
         * showing the chosen image in UI. When user click addImage field, this activity will be
         * launched.
         */
        ActivityResultCallback<Uri> choosePhotoCallback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                updateUISelectedImage(result);
            }
        };

        choosePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                choosePhotoCallback);
    }

    /*
     * When user click Post, the image will be uploaded to Firebase Storage, and the post will
     * be saved to realtime database.
     */
    public void postTextViewOnClick(View view) {
        uploadImageToStorage();
    }

    // When user click addImage field, launch choose image from the device activity.
    public void addImageViewOnClick(View view) {
        choosePhoto.launch("image/*");
    }

    //When user click on close, will go back to homepage
    public void closeImageViewOnClick(View view) {
        startActivity(new Intent(NewPostActivity.this, HomepageActivity.class));
        finish();
    }

    // When user choose a photo from device, it will be shown in the UI.
    private void updateUISelectedImage(Uri result) {
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
     * After user has chosen a photo, upload the image to Firebase Storage. The image will be
     * stored under "Posts_Image" branch in storage.
     * After uploading success, download the imageURL from storage by calling
     * downloadImageUrlFromStorage()
     */
    private void uploadImageToStorage() {
        // First check if there is a selected image
        if (imageUri == null) {
            Toast.makeText(NewPostActivity.this,
                    "No image selected", Toast.LENGTH_LONG).show();
            return;
        }

        setEditable(false);
        // show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // get the extension of image, e.g., jpg
        String imageExt = getImageExtension(imageUri);
        final StorageReference imageRef = storageReference
                .child(System.currentTimeMillis() + "." + imageExt);

        uploadImageTask = imageRef.putFile(imageUri);
        uploadImageTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(NewPostActivity.this,
//                            "Upload image to cloud storage successful",
//                            Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "KX: upload image to storage successful");

                    downloadImageUrlFromStorage(imageRef);
                } else {
                    StorageException e = (StorageException) task.getException();

                    progressBar.setVisibility(View.INVISIBLE);
                    setEditable(true);
                    Toast.makeText(NewPostActivity.this,
                            "Can't upload image to cloud storage", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: upload " + String.valueOf(e.getErrorCode()));
                }
            }
        });
    }

    /*
     * After upload image success, download imageURL from storage. The imageURL will be used to
     * reference the image and stored as post info in database.
     * Once download success, save post info by calling uploadPostToDatabase();
     */
    private void downloadImageUrlFromStorage(final StorageReference imageRef) {

        setEditable(false);
        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(NewPostActivity.this,
//                            "Download image from cloud storage successful",
//                            Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "KX: Download image from storage successful");

                    Uri downloadUri = (Uri) task.getResult();
                    String downloadUriStr = downloadUri.toString();
//                    Log.d(TAG, "KX: Download image " + downloadUriStr);

                    uploadPostToDatabase(downloadUriStr);
                } else {
                    StorageException e = (StorageException) task.getException();

                    progressBar.setVisibility(View.INVISIBLE);
                    setEditable(true);
                    Toast.makeText(NewPostActivity.this,
                            "Can't Download image from cloud storage", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: download " + String.valueOf(e.getErrorCode()));
                }
            }
        });
    }

    /*
     * save post info in realtime database. The post info include postID, authorID, imageURL,
     * content and so on.
     * The post info will be saved under 2 root directory in database. One named "Posts", under
     * the postID. The other one is "User-Posts", under the authorID, and further postID.
     * Once success, show success message to user and go back to homepage.
     */
    private void uploadPostToDatabase(String downloadUriStr) {
//        Log.d(TAG, "KX: Begin to update database");

        setEditable(false);

        String postID = databaseReference.child("Posts").push().getKey();
//        Log.d(TAG, "KX: postID " + postID);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.d(TAG, "KX: can't get current author");
        }

        String authorID = user.getUid();
//        Log.d(TAG, "KX: authorId " + authorID);

        String postContent = postContentEditText.getText().toString();
//        Log.d(TAG, "KX: content" + postContent);

        Post post = new Post(postID, postContent, downloadUriStr, authorID);
        // store post info in a coressponding hashmap
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        // To save post info under 2 root directories in database.
        childUpdates.put("/Posts/" + postID, postValues);
        childUpdates.put("/User-Posts/" + authorID + "/" + postID, postValues);

        databaseReference.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewPostActivity.this,
                            "Post success", Toast.LENGTH_LONG).show();

                    goBackHomepage();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    setEditable(true);
                    DatabaseException e = (DatabaseException) task.getException();

                    Log.d(TAG, "KX: update realtime databsae error - " + e.getMessage().toString());
                }
            }
        });
    }

    // Back to homepage
    private void goBackHomepage() {
        startActivity(new Intent(NewPostActivity.this , HomepageActivity.class));
        finish();
    }

    // Get the extension of a image, e.g., jpg, png, etc.
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