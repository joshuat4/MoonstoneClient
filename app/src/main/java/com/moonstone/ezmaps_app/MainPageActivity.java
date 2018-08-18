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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logOutButton, databaseTestButton;
    private FirebaseAuth mAuth;
    private TextView welcomeText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        db = FirebaseFirestore.getInstance();

        logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(this);

        databaseTestButton = findViewById(R.id.databaseTestButton);
        databaseTestButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + mAuth.getCurrentUser().getEmail());


    }

    private void userSignOut(){
        mAuth.signOut();
        startActivity(new Intent(this, UserIdentification.class));
    }

    private void testDatabase(){
        String email = mAuth.getCurrentUser().getEmail();
        final String Uid = mAuth.getUid();

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("email", email);
        userMap.put("counter", 0);

        db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               Object a = documentSnapshot.get("email");
               Object b = documentSnapshot.get("counter");
               Toast.makeText(MainPageActivity.this, a.toString(), Toast.LENGTH_SHORT).show();

               db.collection("users").document(Uid).update("counter", Integer.parseInt(b.toString()) + 1);
            }
        });

//        db.collection("users").document(Uid).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(MainPageActivity.this, "Database successfully changed", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.logOutButton:
                userSignOut();
                break;
            case R.id.databaseTestButton:
                testDatabase();
                break;
        }
    }
}
