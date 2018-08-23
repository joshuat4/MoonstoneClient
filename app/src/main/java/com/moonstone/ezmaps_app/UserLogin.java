package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class UserLogin extends AppCompatActivity implements View.OnClickListener{

    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Set it to listen for clicks
        findViewById(R.id.textViewSignUp).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordFieldS);
        mAuth = FirebaseAuth.getInstance();
    }

    private void userLogin(){

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                //Switch to main app
                Intent intent = new Intent(UserLogin.this, MainPage.class);
                //Clears all activities currently active on the stack as the login stage is done now
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else{
                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Invalid login", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
        }
    });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.textViewSignUp:
                //Opens signUpActivity
                startActivity(new Intent(this, UserSignUp.class));
                break;
            case R.id.loginButton:
                userLogin();
                break;
        }
    }
}
