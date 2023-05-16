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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.R;
import com.example.miniinstagram.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private TextView postContentTextView;

    private StorageReference storageReference;
    private StorageTask unloadImageTask;
    private StorageTask downloadUrlFromStorage;
    private DatabaseReference databaseReference;
    private Uri imageUri;
    private Bitmap bitmap;
    private FirebaseAuth auth;
    private String uriStr;
    private ActivityResultLauncher<String> choosePhoto;
    private String TAG = "new post activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        close = findViewById(R.id.closeImageView);
        addImageView = findViewById(R.id.addImageImageView);
        postTextView = findViewById(R.id.postTextView);
        postContentTextView = findViewById(R.id.postContentTextView);

        storageReference = FirebaseStorage.getInstance().getReference("Posts_Image");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        choosePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;
                        Log.d(TAG, "errorcheck: " + imageUri);

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

        unloadImageTask = imageRef.putFile(imageUri);
        unloadImageTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    downloadImageUrlFromStorage(imageRef);
                } else {
                    StorageException e = (StorageException) task.getException();
                    e.getErrorCode();
                    Log.d(TAG, "errorcheck: " + String.valueOf(e.getErrorCode()));
                }
            }
        });


    }

    private void downloadImageUrlFromStorage(final StorageReference imageRef) {
        downloadUrlFromStorage = (StorageTask) imageRef.getDownloadUrl();
        downloadUrlFromStorage.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = (Uri) task.getResult();
                    String downloadUriStr = downloadUri.toString();
                    uploadPostToDatabase(downloadUriStr);
//                    goBackHomepage();
                } else {
                    StorageException e = (StorageException) task.getException();
                    e.getErrorCode();
                    Log.d(TAG, "errorcheck: " + String.valueOf(e.getErrorCode()));
                }
            }
        });
    }

    private void uploadPostToDatabase(String downloadUriStr) {
        DatabaseReference postRef = databaseReference.child("Posts");
        String authorID = auth.getCurrentUser().getUid();
        String postID = postRef.push().getKey();
        String content = postContentTextView.getText().toString();

        Post post = new Post(postID, content, downloadUriStr, authorID);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Posts/" + postID, postValues);
        childUpdates.put("/User-Posts/" + authorID + "/" + postID, postValues);

        databaseReference.updateChildren(childUpdates);
    }

    private void goBackHomepage() {
        startActivity(new Intent(NewPostActivity.this , HomepageActivity.class));
        finish();
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}