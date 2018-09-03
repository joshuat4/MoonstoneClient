package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSignUp extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    @BindView(R.id.nameField) EditText _nameField;
    @BindView(R.id.emailField) EditText _emailField;
    @BindView(R.id.passwordField) EditText _passwordField;
    @BindView(R.id.signUpButton) Button _signUpButton;
    @BindView(R.id.textViewLogin) TextView _loginLink;
    @BindView(R.id.progressBar) ProgressBar _progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        _loginLink.setOnClickListener(this);
        _signUpButton.setOnClickListener(this);

    }


    /* This method automatically logs user in
    @Override
    protected void onStart(){
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(UserSignUp.this, MainActivity.class);
            startActivity(intent);
        }
    }
    */

    private void registerUser(){
        final String name = _nameField.getText().toString().trim();
        final String email = _emailField.getText().toString().trim();
        String password = _passwordField.getText().toString().trim();

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


        _progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    User user = new User(name, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            _progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserSignUp.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.input_error_email_registered), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        });

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            case R.id.textViewLogin:
                startActivity(new Intent(this, UserLogin.class));
                break;
        }
    }
}
