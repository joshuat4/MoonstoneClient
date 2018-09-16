package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Menu;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private StorageReference mStorageRef;
    private EditText _editNameField;
    private EditText _editEmailField;
    private TextView _editImage;
    private CircleImageView _editProfilePic;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private boolean textChanged = false;
    private ActionBar actionBar;
    private BottomSheetLayout bottomSheet;

    private String name;
    private String email;
    private String profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        _editNameField = (EditText) findViewById(R.id.editName);
        _editEmailField = (EditText) findViewById(R.id.editEmail);
        _editImage = (TextView) findViewById(R.id.editImage);
        _editProfilePic = (CircleImageView) findViewById(R.id.editProfilePic);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Edit Profile");

        _editImage.setOnClickListener(this);

        name = Tab1Fragment.getName();
        email = Tab1Fragment.getEmail();
        profilePic = Tab1Fragment.getProfilePic();

        _editNameField.setText(name);
        _editEmailField.setText(email);
        Picasso.get().load(profilePic).into(_editProfilePic);


        _editNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EDITPROFILE", "ON NAME CHANGED");
                textChanged = true;
                invalidateOptionsMenu(); // this invokes onCreateOptionsMenu
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
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu
        // This adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(textChanged) {
            Log.d("EDITPROFILE", "TEXT IS CHANGED!!!");
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }

        return true;
    }


    public void editProfile(){

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final String editName = _editNameField.getText().toString().trim();
        final String editEmail = _editEmailField.getText().toString().trim();
        final String Uid = mAuth.getUid();

        DocumentReference docRef = db.collection("users").document(Uid);

        docRef
                .update("name", editName, "email", editEmail)
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

    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        if(id == R.id.exit){
            finish();
            return true;
        }

        if(id == R.id.done){
            editProfile();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
