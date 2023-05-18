package com.example.miniinstagram.UI_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniinstagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerOrLogInButton;
    private ProgressBar progressBar;
    TextView loginRegisterSwitchTextView;
    private static final String TAG = "MainActivity";

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    Boolean registerModeActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Instagram");

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.UsernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerOrLogInButton = findViewById(R.id.registerOrLoginButton);
        loginRegisterSwitchTextView = findViewById(R.id.loginRegisterSwitchTextView);
        progressBar = findViewById(R.id.progressBar);
        loginRegisterSwitchTextView.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    /*
     * when click on the loginRegisterSwitchTextView, user can switch between login and signup mode.
     * That is to say, the registerOrLogInButton will switch between login and signup mode.
     */
    public void onClick(View view) {
        if (view.getId() == R.id.loginRegisterSwitchTextView) {
            if (registerModeActive) {
                registerModeActive = !registerModeActive;
                registerOrLogInButton.setText("Log In");
                loginRegisterSwitchTextView.setText("or, Sign Up");
            } else {
                registerModeActive = !registerModeActive;
                registerOrLogInButton.setText("Sign Up");
                loginRegisterSwitchTextView.setText("or, Log In");
            }
        }
    }

    /*
     * When registerOrLogInButton is clicked, system will validate the input format first, then
     * execute login or register according to if register mode is active or not.
     */
    public void registerOrLoginClicked(View view) {
        String usernameStr = usernameEditText.getText().toString();
        String emailStr = emailEditText.getText().toString();
        String passwordStr = passwordEditText.getText().toString();

        if (validateForm(usernameStr, emailStr, passwordStr)) {
            progressBar.setVisibility(View.VISIBLE);

            if (registerModeActive) {
                registerNewUser(usernameStr, emailStr, passwordStr);
//                Log.d(TAG, "errorcheck: in onclick "  + usernameAvailableFlag)；
            } else {
                userLogin(emailStr, passwordStr);
            }
        }
    }

    /*
     * check if user input correct forms of email, username and password.
     * If it's register active, user must input proper email, username and password. The password
     * must be at least 6 digits. If it's login mode, username is not required.
     */
    private boolean validateForm(String usernameStr, String emailStr, String passwordStr) {
        boolean isInputValid = true;

        if (TextUtils.isEmpty(emailStr)) {
            emailEditText.setError("Email required");
            emailEditText.requestFocus();
            isInputValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailEditText.setError("Proper email required");
            emailEditText.requestFocus();
            isInputValid = false;
        }
        if (TextUtils.isEmpty(passwordStr) || passwordStr.length() < 6) {
            passwordEditText.setError("Password required with at least 6 characters");
            passwordEditText.requestFocus();
            isInputValid = false;
        }
        if (registerModeActive && TextUtils.isEmpty(usernameStr)) {
            usernameEditText.setError("Username required when register");
            usernameEditText.requestFocus();
            isInputValid = false;
        }

        return isInputValid;
    }

    /*
     * Register new user. First create a user in firebase authentication with email and password.
     * If success, then save new userinfo in database by calling writeNewUser().
     * If failed, display error message to the user according to error type.
     */
    private void registerNewUser(String usernameStr, String emailStr, String passwordStr) {
//        Log.d(TAG, "errorcheck: in register " + usernameAvailableFlag);

        // create user in firebase authentication with email and password.
        auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
//                        Toast.makeText(MainActivity.this, "Register processes", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            //on auth success, then need to save new user in database
                            FirebaseUser user = auth.getCurrentUser();
                            writeNewUser(user.getUid(), user.getEmail(), usernameStr);

//                            Toast.makeText(MainActivity.this,
//                                    "Sign up authentication success", Toast.LENGTH_SHORT).show();
                        } else {
                            //register fails, display a message to the user according to error type
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            progressBar.setVisibility(View.INVISIBLE);
//                            Log.d(TAG, "errorcheck" + e.getErrorCode());

                            if (e.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                emailEditText.setError("Email already in use");
                                emailEditText.requestFocus();
                            } else if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                emailEditText.setError("Proper email is required");
                                emailEditText.requestFocus();
                            } else if (e.getErrorCode().equals("ERROR_OPERATION_NOT_ALLOWED")) {
                                Toast.makeText(MainActivity.this, "Operation denied",
                                        Toast.LENGTH_LONG).show();
                            } else if (e.getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                                passwordEditText.setError("Password is too weak");
                                passwordEditText.requestFocus();
                            }
                        }
                    }
                });
    }

    /*
     * All users are stored in database under root directory "users", under their user ID.
     * All user information is stored as a hashmap.
     * Username is stored when register, other info is optional and can be updated later
     * by user.
     */
    private void writeNewUser(String userID, String emailStr, String usernameStr) {
        DatabaseReference newUserReference = databaseReference.child("Users").child(userID);

        // User's profile include id, username, bio, image, link, phone, gender, birthday
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userID);
        userInfo.put("username", usernameStr);
        userInfo.put("bio", "");
        userInfo.put("imageurl", "default");
        userInfo.put("link", "");
        userInfo.put("phone", "");
        userInfo.put("gender", "");
        userInfo.put("birthday", "");

        newUserReference.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Register success",
                            Toast.LENGTH_LONG).show();

                    goToHomePage();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,
                            "Error occur when accessing database", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
     * User log in function.
     * Validate user's email and password in Firebase authentication. If success, go to Homepage.
     * If failed, display error message to user according to error type.
     */
    private void userLogin(String emailStr, String passwordStr) {

        auth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Log in success.",
                            Toast.LENGTH_LONG).show();
                    goToHomePage();
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.d(TAG, "errorcheck: signInWithEmail:failure " + task.getException().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Log in authentication failed.",
                            Toast.LENGTH_LONG).show();

                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
//                    Log.d(TAG, "errorcheck" + e.getErrorCode());

                    if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                        passwordEditText.setError("Password is incorrect");
                        passwordEditText.requestFocus();
                    } else if (e.getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                        emailEditText.setError("User not found");
                        emailEditText.requestFocus();
                    }
                }
            }
        });
    }

    // Go to Homepage
    private void goToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}