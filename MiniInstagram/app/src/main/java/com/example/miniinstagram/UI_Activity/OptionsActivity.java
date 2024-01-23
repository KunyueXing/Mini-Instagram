package com.example.miniinstagram.UI_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniinstagram.R;
import com.google.firebase.auth.FirebaseAuth;

public class OptionsActivity extends AppCompatActivity {
    private ImageView closeImageView;
    private TextView logoutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        closeImageView = findViewById(R.id.closeImageView);
        logoutTextView = findViewById(R.id.logoutTextView);
    }

    public void logoutOnClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(OptionsActivity.this , MainActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    public void closeImageViewOnClick (View view) {
        finish();
    }
}