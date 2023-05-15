package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniinstagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

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
    private FirebaseAuth auth;
    private String uriStr;


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

        //when user click on close, will go back to homepage
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewPostActivity.this, HomepageActivity.class));
                finish();
            }
        });

    }
}