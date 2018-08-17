package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logOutButton;
    private FirebaseAuth mAuth;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + mAuth.getCurrentUser().getEmail());


    }

    private void userSignOut(){
        mAuth.signOut();
        startActivity(new Intent(this, UserIdentification.class));
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.logOutButton:
                userSignOut();
                break;
        }
    }
}
