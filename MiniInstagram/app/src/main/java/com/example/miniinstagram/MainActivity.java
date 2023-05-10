package com.example.miniinstagram;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private ActivityMainBinding binding;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    TextView loginRegisterSwitchTextView;
    Boolean registerModeActive = true;
    private static final String TAG = "MainActivity";

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    // when flag == 0. means not available or not success.
    private int writeNewUserSuccessFlag = 0;
    private int usernameAvailableFlag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Instagram");

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.UsernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerOrLoginButton);
        loginRegisterSwitchTextView = findViewById(R.id.loginRegisterSwitchTextView);
        loginRegisterSwitchTextView.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    //when click on the loginRegisterSwitchTextView, user can switch from login or signup mode
    public void onClick(View view) {
        if (view.getId() == R.id.loginRegisterSwitchTextView) {
            if (registerModeActive) {
                registerModeActive = !registerModeActive;
                registerButton.setText("Log In");
                loginRegisterSwitchTextView.setText("or, Sign Up");
            } else {
                registerModeActive = !registerModeActive;
                registerButton.setText("Sign Up");
                loginRegisterSwitchTextView.setText("or, Log In");
            }
        }
    }

    public void registerOrLoginClicked(View view) {
        String usernameStr = usernameEditText.getText().toString();
        String emailStr = emailEditText.getText().toString();
        String passwordStr = passwordEditText.getText().toString();

        if (validateForm(usernameStr, emailStr, passwordStr)) {
            if (registerModeActive) {
                //Toast.makeText(MainActivity.this, "Register active", Toast.LENGTH_SHORT).show();
                registerNewUser(usernameStr, emailStr, passwordStr);
            } else {
                userLogin(emailStr, passwordStr);
            }
        }
    }


    /*
     * check if user input correct forms of email, username and password.
     * If it's registeractive, user must input email, username and password. The password must be
     * at least 6 digits.
     * If it's login mode, username is not required
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

    private void registerNewUser(String usernameStr, String emailStr, String passwordStr) {

        auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        // Toast.makeText(MainActivity.this, "Register processes", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            //register success, then update new user in database under branch "Users"
                            onAuthSuccess(auth.getCurrentUser(), usernameStr);
                        } else {
                            //register fails, display a message to the user according to error type
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Log.d(TAG, "errorcheck" + e.getErrorCode());

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



    private void onAuthSuccess(FirebaseUser user, String usernameStr) {
        //Write new user, if successful, return true, and new user stay logged in
        writeNewUser(user.getUid(), user.getEmail(), usernameStr);
//        Toast.makeText(MainActivity.this, "on Auth success", Toast.LENGTH_LONG).show();

        //If write new user success, go to HomepageActivity
        if (writeNewUserSuccessFlag != 0) {

        }
    }

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
                    writeNewUserSuccessFlag = 1;
//                    Toast.makeText(MainActivity.this, "Write in database success", Toast.LENGTH_LONG).show();
//                    Toast.makeText(MainActivity.this, userID, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void userLogin(String emailStr, String passwordStr) {

    }
}