package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Menu;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import android.text.TextWatcher;
import android.text.Editable;
import android.support.v7.app.ActionBar;

import android.support.annotation.NonNull;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.squareup.picasso.Picasso;

import android.view.LayoutInflater;
import android.app.Activity;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements OnClickListener {

    private Toolbar _toolbar;

    private EditText _editNameField;
    private EditText _editEmailField;
    private EditText _editPasswordField;

    private boolean emailChanged = false;
    private boolean nameChanged = false;
    private boolean passwordChanged = false;

    private TextView _editImage;
    private Button _signOutButton;
    private CircleImageView _editProfilePic;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        _editNameField = (EditText) findViewById(R.id.editName);
        _editEmailField = (EditText) findViewById(R.id.editEmail);
        _editPasswordField = (EditText) findViewById(R.id.editPassword);
        _editProfilePic = (CircleImageView) findViewById(R.id.editProfilePic);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);

        _editImage = (TextView) findViewById(R.id.editImage);
        _signOutButton = (Button) findViewById(R.id.signOutButton);

        // Toolbar
        setSupportActionBar(_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Edit Profile");
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        loadProfileInfo();

        _editImage.setOnClickListener(this);
        _signOutButton.setOnClickListener(this);
        _editNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("EDITPROFILE", "AFTER NAME CHANGED");
                nameChanged = true;
                invalidateOptionsMenu();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

        _editEmailField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("EDITPROFILE", "AFTER EMAIL CHANGED");
                emailChanged = true;
                invalidateOptionsMenu();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

        _editPasswordField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("EDITPROFILE", "AFTER PASSWORD CHANGED");
                passwordChanged = true;
                invalidateOptionsMenu();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editImage:
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "BottomSheetDialog");
                break;
            case R.id.signOutButton:
                FirebaseAuth.getInstance().signOut();

                // Restarts
                Intent intent = new Intent(this, FrontPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void loadProfileInfo(){
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            _editNameField.setText(name);
            _editEmailField.setText(email);
            Picasso.get().load(photoUrl).into(_editProfilePic);

        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.cancel){
            hideKeyboard(this);
            this.getCurrentFocus().clearFocus();

            loadProfileInfo();

            nameChanged = false;
            emailChanged = false;
            passwordChanged = false;
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.done){
            hideKeyboard(this);
            this.getCurrentFocus().clearFocus();

            if(nameChanged){
                editName();
            }

            if(emailChanged){
                editEmail();
            }

            loadProfileInfo();

            nameChanged = false;
            emailChanged = false;
            passwordChanged = false;
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu
        // This adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(nameChanged || emailChanged || passwordChanged) {
            Log.d("EDITPROFILE", "TEXT IS CHANGED!!!");
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);

        }else{
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }

        return true;
    }


    public void editName(){

        final String editName = _editNameField.getText().toString().trim();

        // This goes to mAUTH
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EDITPROFILE", "User profile updated.");
                        }
                    }
                });



        // This goes to Cloud Firestore
        final String Uid = mAuth.getUid();
        DocumentReference docRef = db.collection("users").document(Uid);

        docRef
                .update("name", editName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EDITPROFILE", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EDITPROFILE", "Error updating document", e);
                    }
                });

    }

    public void editEmail(){
        final String editEmail = _editEmailField.getText().toString().trim();

        // This goest o

        if (user != null) {

            user.updateEmail(editEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("EDITPROFILE", "User email address updated.");
                            }
                        }
                    });

        } else {
            Log.d("EDITPROFILE", "User Not Signed In");
        }

        // This goes to Cloud Firestore
        final String Uid = mAuth.getUid();
        DocumentReference docRef = db.collection("users").document(Uid);
        docRef
                .update("email", editEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EDITPROFILE", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EDITPROFILE", "Error updating document", e);
                    }
                });
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
