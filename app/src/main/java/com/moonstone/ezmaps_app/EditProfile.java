package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Menu;

import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.OnClick;

public class EditProfile extends AppCompatActivity implements OnClickListener {

    private Toolbar toolbar;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv=(TextView)findViewById(R.id.editImage);

        tv.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //perform your action here
            }
        });


            //mStorageRef = FirebaseStorage.getInstance().getReference();



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


    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        if(id == R.id.exit){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
