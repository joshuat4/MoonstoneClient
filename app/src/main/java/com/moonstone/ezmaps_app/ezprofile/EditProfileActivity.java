package com.moonstone.ezmaps_app.ezprofile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Menu;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import android.text.TextWatcher;
import android.text.Editable;
import android.support.v7.app.ActionBar;

import android.support.annotation.NonNull;

import com.moonstone.ezmaps_app.FrontPageActivity;
import com.moonstone.ezmaps_app.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

//the class takes care of everything that have to do with profile editing in the app.
public class EditProfileActivity extends AppCompatActivity implements OnClickListener {

    //fields
    private Toolbar _toolbar;

    private EditText _editNameField;
    private TextInputEditText _editEmailField;
    private TextInputLayout _editEmailLayout;
    private EditText _editPasswordField;
    private ProgressBar _progressBar;

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


    //oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get the respective layout elements for this activity
        setContentView(R.layout.activity_edit_profile);
        _editNameField = (EditText) findViewById(R.id.editName);

        _editEmailField = (TextInputEditText) findViewById(R.id.editEmail);
        _editEmailLayout = (TextInputLayout) findViewById(R.id.editEmailLayout);
        _editPasswordField = (EditText) findViewById(R.id.editPassword);
        _editProfilePic = (CircleImageView) findViewById(R.id.editProfilePic);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);

        _progressBar = (ProgressBar) findViewById(R.id.progressBar);

        _editImage = (TextView) findViewById(R.id.editImage);
        _signOutButton = (Button) findViewById(R.id.signOutButton);

        // Toolbar
        setSupportActionBar(_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.close_dark);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Edit Profile");
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //this is required to access the DB
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        loadProfileInfo();

        //sets onclick listeners to the interactive buttons in the activity
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
    public void onResume(){
        super.onResume();
    }


    //deal with clicks on the app.
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editImage:
                UploadDialogFragment bottomSheet = new UploadDialogFragment();
                bottomSheet.show(getSupportFragmentManager(), "com.moonstone.ezmaps_app.profiles.UploadDialogFragment");
                break;
            case R.id.signOutButton:
                //logout
                FirebaseAuth.getInstance().signOut();

                // Restarts
                Intent intent = new Intent(this, FrontPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    //its a function that deals with UI accessibility.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    //load profile information from the DB
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
            loadProfileInfo();

            nameChanged = false;
            emailChanged = false;
            passwordChanged = false;
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.done){
            if(nameChanged){
                editName();
            }

            if(emailChanged){
                _progressBar.setVisibility(View.VISIBLE);
                editEmail();
            }


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

    //when name is tapped on to edit, this function is called.
    public void editName(){

        final String editName = _editNameField.getText().toString().trim();

        // This goes to mAUTH
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editName)
                .build();

        //update the user profile with the new display name
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EDITPROFILE", "User profile updated.");

                            // This goes to Cloud Firestore
                            DocumentReference docRef = db.collection("users").document(mAuth.getUid());

                            docRef
                                    .update("name", editName)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("EDITPROFILE", "DocumentSnapshot successfully updated!");
                                            _progressBar.setVisibility(View.GONE);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("EDITPROFILE", "Error updating document", e);
                                        }
                                    });

                        }
                    }
                });

    }

    //function is called when email field is being edited/tapped on
    public void editEmail(){
        final String editEmail = _editEmailField.getText().toString().trim();
        if (user != null) {


            //update the email of the user through the DB
            user.updateEmail(editEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("EDITPROFILE", "User email address updated.");

                                // This goes to Cloud Firestore
                                DocumentReference docRef = db.collection("users").document(mAuth.getUid());
                                docRef
                                        .update("email", editEmail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("EDITPROFILE", "DocumentSnapshot successfully updated!");
                                                _progressBar.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("EDITPROFILE", "Error updating document", e);
                                                _progressBar.setVisibility(View.GONE);
                                            }
                                        });

                            }else{
                                Log.d("EDITPROFILE", "Can't change email");
                                Toast.makeText(getApplicationContext(), "Email either already registered, or last login too long ago", Toast.LENGTH_SHORT).show();

                                _progressBar.setVisibility(View.GONE);
                                _editEmailLayout.setError("Email already registered, or last login too long ago");
                                loadProfileInfo();


                            }
                        }
                    });

        } else {
            Log.d("EDITPROFILE", "User Not Signed In");
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



}
