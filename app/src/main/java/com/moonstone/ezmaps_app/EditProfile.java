package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.text.TextWatcher;
import android.text.Editable;
import android.support.v7.app.ActionBar;

import java.util.Map;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.OnClick;

public class EditProfile extends AppCompatActivity implements OnClickListener {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private EditText editNameField;
    private EditText editEmailField;
    private TextView editImage;
    private FirebaseFirestore db;

    private String name;
    private String email;

    private boolean textChanged = false;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editNameField = (EditText) findViewById(R.id.editName);
        editEmailField = (EditText) findViewById(R.id.editEmail);
        editImage = (TextView) findViewById(R.id.editImage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Edit Profile");

        editImage.setOnClickListener(this);

        name = Tab1Fragment.getName();
        email = Tab1Fragment.getEmail();
        editNameField.setText(name);
        editEmailField.setText(email);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //mStorageRef = FirebaseStorage.getInstance().getReference();

        editNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("EDITPROFILE", "AFTER TEXT CHANGED");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("EDITPROFILE", "BEFORE TEXT CHANGED");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EDITPROFILE", "ON TEXT CHANGED");
                textChanged = true;
                invalidateOptionsMenu(); // this invokes onCreateOptionsMenu
            }

        });

    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editImage:
                //Intent intent = new Intent(getActivity(), EditProfile.class);
                //startActivity(intent);
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
        final String editName = editNameField.getText().toString().trim();
        final String editEmail = editEmailField.getText().toString().trim();
        final String Uid = mAuth.getUid();

        DocumentReference docRef = db.collection("users").document(Uid);
        /*
        if(editName.equals(name)){
            // Update Name
            Map<String, Object> updateName = new HashMap<>();
            updateName.put("name", editName);
            ApiFuture<WriteResult> writeResult = docRef.update(updateName);
            System.out.println("Update time : " + writeResult.get().getUpdateTime());

        }


        if(editEmail.equals(email)){



        }

        */


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
