package com.moonstone.ezmaps_app;

import android.content.Intent;
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
import java.util.ArrayList;

import butterknife.ButterKnife;


import butterknife.BindView;

public class Tab1Fragment extends Fragment implements OnClickListener{

    private Button editProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText _nameField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        _nameField = (EditText)view.findViewById(R.id.text1);

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Gettingdata();

        return view;
    }



    private void Gettingdata() {
        final String Uid = mAuth.getUid();

        db.collection("users").document("JGMgxb1apTgnHQm7fi5zB8tsOtM2")
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("firestore", "FIRESTORE SUCCESS!!");

                    final String name;
                    final String email;
                    final String profilePic;

                    if (documentSnapshot!=null) {

                        Log.d("firestore", "DOCUMENT NOT NULL!!");

                        name = documentSnapshot.getString("name");
                        // name = documentSnapshot.get("name").toString();

                        _nameField.setText(name);

                    } else {
                        name="else case";
                        Log.d("documentSnapshot", "else case");
                        //Toast.makeText(this, "Document Does Not exists", Toast.LENGTH_SHORT).show();
                    }
                }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(Exception e) {
                // Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                Log.d("Tag",e.toString());
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editProfileButton:
                // Difference between starting an Activity from Fragment and Activity is
                // how you get context (getActivity(), this).
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
                break;
        }
    }


}
