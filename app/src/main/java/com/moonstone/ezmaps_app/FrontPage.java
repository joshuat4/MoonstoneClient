package com.moonstone.ezmaps_app;


import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.ButterKnife;
import butterknife.BindView;


public class FrontPage extends AppCompatActivity {

    @BindView(R.id.loginButton) Button _loginButton;
    @BindView(R.id.signUpButton) Button _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FirebaseToken", "Init token "+ FirebaseInstanceId.getInstance().getToken());

        setContentView(R.layout.activity_front_page);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPage.this, UserSignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
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

