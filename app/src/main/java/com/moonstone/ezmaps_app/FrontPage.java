package com.moonstone.ezmaps_app;


import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;


public class FrontPage extends AppCompatActivity {

    @BindView(R.id.loginButton) Button _loginButton;
    @BindView(R.id.signUpButton) TextView _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPage.this, UserSignUp.class);
                startActivity(intent);

            }
        });

        _loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPage.this, UserLogin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }


}

