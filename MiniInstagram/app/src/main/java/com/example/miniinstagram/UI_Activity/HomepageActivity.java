package com.example.miniinstagram.UI_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.miniinstagram.R;
import com.example.miniinstagram.fragments.HomeFragment;
import com.example.miniinstagram.fragments.ProfileFragment;
import com.example.miniinstagram.fragments.NotificationFragment;
import com.example.miniinstagram.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomepageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNaviView;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        bottomNaviView = findViewById(R.id.bottom_navigation);

        // Pass data between components. The data is stored in Bundle.
        // When there's content in bundle, open the profile fragment of the userID that is transferred.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String profileUserID = bundle.getString("profileUserID");

            // transfer data from activity to a fragment of the same activity
            SharedPreferences.Editor editor = getSharedPreferences("PROFILE", MODE_PRIVATE).edit();
            editor.putString("profileUserID", profileUserID);
            editor.apply();

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.fragment_container, new ProfileFragment())
                                       .commit();
        } else {
            // Be default, we start Home Fragment.
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.fragment_container, new HomeFragment())
                                       .commit();
        }



        // Connect to search, add new post, notification and profile fragmentpage in navigation bar.
        bottomNaviView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // find the corresponding page according to which icon is clicked by user
                goToSelectedFragments(item.getItemId());

                // go to the corresponding page
                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }

    /*
     * According to the id of which icon user clicked, set the corresponding fragment accordingly
     * and stored the fragment in variable selectedFragment
     */
    private void goToSelectedFragments(int itemId) {
        if (itemId == R.id.nav_add) {
            Intent intent = new Intent(HomepageActivity.this, NewPostActivity.class);
            startActivity(intent);
            finish();
        } else if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_notifi) {
            selectedFragment = new NotificationFragment();
        } else if (itemId == R.id.nav_person) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_search) {
            selectedFragment = new SearchFragment();
        }
    }
}