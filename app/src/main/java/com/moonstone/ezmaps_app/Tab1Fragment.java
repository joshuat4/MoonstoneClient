package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.EditText;
import android.content.res.Resources;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentReference;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import android.util.Log;


import butterknife.ButterKnife;


import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Tab1Fragment extends Fragment implements OnClickListener{

    private Button editProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView _nameField;
    private TextView _emailField;
    private CircleImageView _profilePic;

    private static String name;
    private static String email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        _nameField = (TextView)view.findViewById(R.id.nameField);
        _emailField = (TextView) view.findViewById(R.id.emailField);
        _profilePic = (CircleImageView) view.findViewById(R.id.profilePic);

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setProfileData();
        name = _nameField.getText().toString();
        email = _emailField.getText().toString();


        return view;
    }


    public static String getName(){
        return name;
    }

    public static void setName(String s){
        name = s;
    }

    public static String getEmail(){
        return email;
    }

    public static void setEmail(String s){
        email = s;
    }


    public static Drawable LoadImageFromWebOperations(String url) {

        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");

            Log.w("DEBUGGERtabl1", "Loading Image");
            return d;
        } catch (Exception e) {
            Log.w("DEBUGGERtabl1", "Failed to Load Image");
            return null;
        }
    }

    private void setProfileData() {
        final String Uid = mAuth.getUid();

        Log.w("DEBUGGERtabl1", "gettingdata");
        db.collection("users").document(Uid)
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("DEBUGGERtabl1", "FIRESTORE SUCCESS!!");

                    if (documentSnapshot.exists()) {

                        Log.d("DEBUGGERtabl1", "DOCUMENT NOT NULL!!");

                        final String name = documentSnapshot.get("name").toString();
                        final String email = documentSnapshot.get("email").toString();
                        final String profilePic = documentSnapshot.get("profilePic").toString();

                        _nameField.setText(name);
                        _emailField.setText(email);
                        setName(name);
                        setEmail(email);

                        Log.d("DEBUGGERtabl1", "SUCCESSS HIP HIP");

                    } else {
                        Log.d("DEBUGGERtabl1", "FIRESTORE FAILED");

                    }
                }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(Exception e) {
                Log.d("Tag",e.toString());
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editProfileButton:
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
                break;
        }
    }


}
