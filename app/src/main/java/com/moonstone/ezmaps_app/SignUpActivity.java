package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    EditText emailField;
    EditText passwordField;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        emailField = findViewById(R.id.emailFieldS);
        passwordField = findViewById(R.id.passwordFieldS);
        progressBar = findViewById(R.id.progressBarSignUp);

        findViewById(R.id.textViewLogIn).setOnClickListener(this);
        findViewById(R.id.signUpButton).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser(){
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(email.isEmpty()){
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Please enter valid email");
            emailField.requestFocus();
            return;
        }

        if(password.length()<5){
            passwordField.setError("Password is required to be longer than 5 characters");
            passwordField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            //Anonymous listener class
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User Register Successful", Toast.LENGTH_SHORT).show();
                    //Switch to main app
                    Intent intent = new Intent(SignUpActivity.this, MainPageActivity.class);
                    //Clears all activities currently active on the stack as the login stage is done now
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                    }
                    else{
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
                registerUser();
                break;
            case R.id.textViewLogIn:
                startActivity(new Intent(this, UserIdentification.class));
                break;
        }
    }
}
