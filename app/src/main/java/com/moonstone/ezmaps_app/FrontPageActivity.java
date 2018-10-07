package com.moonstone.ezmaps_app;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import butterknife.ButterKnife;
import butterknife.BindView;


public class FrontPageActivity extends AppCompatActivity {

    @BindView(R.id.loginButton) Button _loginButton;
    @BindView(R.id.signUpButton) Button _signupButton;

    /* DELETE HERE AFTER TESTING */
    @BindView(R.id.test1) Button _test1;
    @BindView(R.id.test2) Button _test2;
    private FirebaseAuth mAuth;
    /* DELETE HERE AFTER TESTING */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_front_page);
        ButterKnife.bind(this);


        /* DELETE HERE AFTER TESTING */
        mAuth = FirebaseAuth.getInstance();
        _test1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin("test@test.com","testing");
            }
        });

        _test2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin("test2@test2.com","test2test2");

            }
        });
        /* DELETE HERE AFTER TESTING */

        _signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPageActivity.this, UserSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        _loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPageActivity.this, UserLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }

    private void userLogin(String email, String password){

        // Authenticate the user
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(FrontPageActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        } else {

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


}

