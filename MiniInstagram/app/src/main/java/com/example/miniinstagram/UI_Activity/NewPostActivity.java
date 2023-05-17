package com.example.miniinstagram.UI_Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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

    private ImageView close;
    private ImageView addImageView;
    private TextView postTextView;
    private EditText postContentEditText;

    private StorageReference storageReference;
    private StorageTask uploadImageTask;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private Uri imageUri;
    private Bitmap bitmap;
    private ActivityResultLauncher<String> choosePhoto;
    private String TAG = "new post activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        close = findViewById(R.id.closeImageView);
        addImageView = findViewById(R.id.addImageImageView);
        postTextView = findViewById(R.id.postTextView);
        postContentEditText = findViewById(R.id.postContentEditText);

        // Images will be stored in Firebase Storage, under "Posts_Image"
        storageReference = FirebaseStorage.getInstance().getReference("Posts_Image");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        /*
         * set an activity of choosing a photo from device, saving result in variable imageUri and
         * showing the chosen image in UI. When user click addImage field, this activity will be
         * launched.
         */
        choosePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;
                        Log.d(TAG, "KX: " + imageUri);

                        updateUISelectedImage();
                    }
                });

        //when user click on close, will go back to homepage
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewPostActivity.this, HomepageActivity.class));
                finish();
            }
        });

        // When user click addImage field, launch choose image from the device activity.
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto.launch("image/*");
            }
        });

        postTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToStorage();
            }
        });
    }

    private void updateUISelectedImage() {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                    imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addImageView.setImageBitmap(bitmap);
    }

    private void uploadImageToStorage() {
        // First check if there is a selected image
        if (imageUri == null) {
            Toast.makeText(NewPostActivity.this,
                    "No image selected", Toast.LENGTH_LONG).show();
            return;
        }

        String imageExt = getImageExtension(imageUri);
        final StorageReference imageRef = storageReference
                .child(System.currentTimeMillis() + "." + imageExt);

        uploadImageTask = imageRef.putFile(imageUri);
        uploadImageTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NewPostActivity.this,
                            "Upload image to cloud storage successful",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: upload image to storage successful");

                    downloadImageUrlFromStorage(imageRef);
                } else {
                    StorageException e = (StorageException) task.getException();

                    Toast.makeText(NewPostActivity.this,
                            "Can't upload image to cloud storage", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: upload " + String.valueOf(e.getErrorCode()));
                }
            }
        });

    }

    private void downloadImageUrlFromStorage(final StorageReference imageRef) {

        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NewPostActivity.this,
                            "Download image from cloud storage successful",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: Download image from storage successful");

                    Uri downloadUri = (Uri) task.getResult();
                    String downloadUriStr = downloadUri.toString();
                    Log.d(TAG, "KX: Download image " + downloadUriStr);

                    uploadPostToDatabase(downloadUriStr);
                } else {
                    StorageException e = (StorageException) task.getException();

                    Toast.makeText(NewPostActivity.this,
                            "Can't Download image from cloud storage", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "KX: download " + String.valueOf(e.getErrorCode()));
                }
            }
        });
    }

    private void uploadPostToDatabase(String downloadUriStr) {
//        Log.d(TAG, "KX: Begin to update database");

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
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/Posts/" + postID, postValues);
        childUpdates.put("/User-Posts/" + authorID + "/" + postID, postValues);

        databaseReference.updateChildren(childUpdates);

//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(NewPostActivity.this,
//                            "Update database success", Toast.LENGTH_LONG).show();
//
////                    goBackHomepage();
//                } else {
//                    DatabaseException e = (DatabaseException) task.getException();
//
//                    Log.d(TAG, "KX: update realtime databsae error - " + e.getMessage().toString());
//                }
//            }
//        });
    }

    private void goBackHomepage() {
        startActivity(new Intent(NewPostActivity.this , HomepageActivity.class));
        finish();
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        Log.d(TAG, "KX: get extension " + extension);
        return extension;
    }
}