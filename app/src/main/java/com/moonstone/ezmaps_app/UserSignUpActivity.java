package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;
import com.moonstone.ezmaps_app.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String qrcodeURL;
    public static Boolean signUpInProgress = false;

    @BindView(R.id.emailField) EditText _emailField;
    @BindView(R.id.passwordField) EditText _passwordField;
    @BindView(R.id.nameField) EditText _nameField;
    @BindView(R.id.signUpButton) Button _signUpButton;
    @BindView(R.id.textViewLogin) TextView _loginLink;
    @BindView(R.id.progressBar) ProgressBar _progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        _loginLink.setOnClickListener(this);
        _signUpButton.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }

    private void registerUser(){
        final String name = _nameField.getText().toString().trim();
        final String email = _emailField.getText().toString().trim();
        final String password = _passwordField.getText().toString().trim();

        if(name.isEmpty()){
            _nameField.setError(getString(R.string.input_error_name));
            _nameField.requestFocus();
            return;
        }

        if(email.isEmpty()){
            _emailField.setError(getString(R.string.input_error_email));
            _emailField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _emailField.setError(getString(R.string.input_error_emaiL_invalid));
            _emailField.requestFocus();
            return;
        }

        if(password.isEmpty()){
            _passwordField.setError(getString(R.string.input_error_password));
            _passwordField.requestFocus();
            return;
        }

        if(password.length()<5){
            _passwordField.setError(getString(R.string.input_error_password_length));
            _passwordField.requestFocus();
            return;
        }

        signUpInProgress = true;
        _progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(getApplicationContext(), "User Register Successful", Toast.LENGTH_SHORT).show();
                   //Switch to main app
                   final Intent intent = new Intent(UserSignUpActivity.this, MainActivity.class);
                   //Clears all activities currently active on the stack as the login stage is done now
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                   qrcodeURL = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + email ;

                   //Fields that must be uploaded to firebase

                   final Map<String, Object> userMap = new HashMap<>();
                   final ArrayList<String> contacts = new ArrayList<>();
                   final ArrayList<String> favouritePlaces = new ArrayList<>();
                   final ArrayList<String> friendRequests = new ArrayList<>();
                   final ArrayList<String> groupchats = new ArrayList<>();
                   final ArrayList<String> pendingRequests = new ArrayList<>();
                   final String deviceToken = MyFirebaseMessagingService.fetchToken();
                   userMap.put("email", email);
                   userMap.put("contacts", contacts);
                   userMap.put("requests", friendRequests);
                   userMap.put("profilePic", "https://source.unsplash.com/hchKfNuAblU/500x500");
                   userMap.put("name", name);
                   userMap.put("favouritePlaces", favouritePlaces);
                   userMap.put("QRCode", qrcodeURL);
                   userMap.put("groupchats", groupchats);
                   userMap.put("deviceToken", deviceToken);
                   userMap.put("pendingRequests", pendingRequests);


                   //This goes to Cloud Firestore
                   db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           if (!documentSnapshot.exists()) {
                               db.collection("users").document(mAuth.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       //Setup complete
                                       signUpInProgress = false;
                                       startActivity(intent);
                                   }
                               });
                           }
                       }
                   });

                   // This goes to mAuth
                   FirebaseUser user = mAuth.getCurrentUser();
                   UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                           .setDisplayName(name)
                           .setPhotoUri(Uri.parse("https://source.unsplash.com/hchKfNuAblU/500x500"))
                           .build();

                   user.updateProfile(profileUpdates)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       Log.d("SIGNUP", "User profile updated.");
                                   }
                               }
                           });

               } else {

                   _progressBar.setVisibility(View.GONE);
                   if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                       Toast.makeText(getApplicationContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }

               }
            }
        });


    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.signUpButton:
                hideKeyboard(this);
                registerUser();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, UserLoginActivity.class));
                break;
        }
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


    private void updateQRImageToDB(String url){
        final String Uid = mAuth.getUid();
        DocumentReference docRef = db.collection("users").document(Uid);
        docRef
                .update("QRCode", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("UserSignUp", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserSignUp", "Error uploading QRCode", e);
                    }
                });

    }
}
