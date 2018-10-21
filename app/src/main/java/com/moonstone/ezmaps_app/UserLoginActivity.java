package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;
import com.moonstone.ezmaps_app.main.MainActivity;

import butterknife.ButterKnife;
import butterknife.BindView;

public class UserLoginActivity extends AppCompatActivity implements View.OnClickListener{
    String TAG = "UserLoginActivity";



    private String userID;
    private FirebaseAuth mAuth;
    @BindView(R.id.emailField) EditText _emailField;
    @BindView(R.id.passwordField) EditText _passwordField;
    @BindView(R.id.loginButton) Button _loginButton;
    @BindView(R.id.textViewSignUp) TextView _signUpLink;
    @BindView(R.id.progressBar) ProgressBar _progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Setting relevant onclick listeners
        _signUpLink.setOnClickListener(this);
        _loginButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void userLogin(){

        // Retrieve the email and password from user input
        String email = _emailField.getText().toString().trim();
        String password = _passwordField.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            _progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        // Authenticate the user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            _progressBar.setVisibility(View.GONE);
            if(task.isSuccessful()){
                userID = mAuth.getUid();
                final String deviceToken = MyFirebaseMessagingService.fetchToken();
                Log.d("DEBUGGERUserLogin", "UID = "+ userID);
                Log.d("DEBUGGERUserLogin", "DeviceToken = " + deviceToken);

                final FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();
                db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        db.collection("users").document(userID).update("deviceToken", deviceToken);
                        Log.d(TAG, "userLogin: SUCCESSFULLY UPDATED deviceToken to " + deviceToken);
                    }
                });



                Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // clear all activity on stack
                startActivity(intent);


            } else {
                _progressBar.setVisibility(View.GONE);
                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Invalid login. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        }
        }

    });
    }

    //Override physical back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.textViewSignUp:
                startActivity(new Intent(this, UserSignUpActivity.class));
                break;

            case R.id.loginButton:
                hideKeyboard(this);
                _progressBar.setVisibility(View.VISIBLE);
                userLogin();
                break;

        }
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    // HIDE KEYBOARD FOR ACTIVITY
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}
