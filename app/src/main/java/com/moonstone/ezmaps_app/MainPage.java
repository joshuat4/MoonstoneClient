package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.HashMap;
import java.util.Map;

public class MainPage extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @BindView(R.id.logOutButton) Button _logOutButton;
    @BindView(R.id.dbTestButton) Button _dbTestButton;
    @BindView(R.id.welcomeText) TextView _welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        ButterKnife.bind(this);
        _logOutButton.setOnClickListener(this);
        _dbTestButton.setOnClickListener(this);
        _welcomeText.setText("Welcome " + mAuth.getCurrentUser().getEmail());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void userSignOut(){
        mAuth.signOut();
        startActivity(new Intent(this, UserLogin.class));
    }

    private void testDatabase(){
        String email = mAuth.getCurrentUser().getEmail();
        final String Uid = mAuth.getUid();

        final Map<String, Object> userMap = new HashMap<>();

        userMap.put("email", email);
        userMap.put("counter", 0);

        db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    db.collection("users").document(Uid).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainPage.this, "Database successfully changed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Object a = documentSnapshot.get("email");
                    Object b = documentSnapshot.get("counter");
                    Toast.makeText(MainPage.this, a.toString(), Toast.LENGTH_SHORT).show();

                    db.collection("users").document(Uid).update("counter", Integer.parseInt(b.toString()) + 1);
                }
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.logOutButton:
                userSignOut();
                break;
            case R.id.dbTestButton:
                testDatabase();
                break;
        }
    }


    //----------------------------Helper functions----------------------------------------//
    //Generate and show a toast message containing specified string
    public void toastMessage(String s){
        Toast.makeText(MainPage.this, s, Toast.LENGTH_SHORT).show();
    }
}
