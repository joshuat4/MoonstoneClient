package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import com.google.firebase.database.FirebaseDatabase;

public class Tab1Fragment extends Fragment implements OnClickListener{
    private Button editProfileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        return view;
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
