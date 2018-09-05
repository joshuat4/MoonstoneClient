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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import butterknife.BindView;

public class Tab1Fragment extends Fragment implements OnClickListener{

    private Button editProfileButton;
    private DatabaseReference fbRef;
    private DatabaseReference profileRef;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private User user;
    @BindView(R.id.nameField) TextView _nameField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        loadProfileInfo();
        setProfileInfo();

        return view;
    }


    private void setProfileInfo(){
        _nameField.setText(user.getName());

    }

    private void loadProfileInfo(){

        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fbUser.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    user = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Tab1Fragment", "loadLog:onCancelled", databaseError.toException());
            }
        };
        fbRef.addValueEventListener(postListener);

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
