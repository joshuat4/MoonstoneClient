package com.moonstone.ezmaps_app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezprofile.EditProfileActivity;
import com.moonstone.ezmaps_app.qrcode.QRCodeActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab1Fragment extends Fragment implements OnClickListener{

    private Button editProfileButton;
    private Button _QRButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView _nameField;
    private TextView _emailField;
    private CircleImageView _profilePic;

    public static Tab1Fragment newInstance() {
        Tab1Fragment fragment = new Tab1Fragment();
        return fragment;
    }

    public Tab1Fragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d("TAB1", "SUCCESS");
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        _nameField = (TextView)view.findViewById(R.id.nameField);
        _emailField = (TextView) view.findViewById(R.id.emailField);
        _profilePic = (CircleImageView) view.findViewById(R.id.profilePic);
        _QRButton = (Button) view.findViewById(R.id.QRButton);
        _QRButton.setOnClickListener(this);

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        setProfileData();

        return view;
    }

    private void setProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            _nameField.setText(name);
            _emailField.setText(email);
            Picasso.get().load(photoUrl).into(_profilePic);

        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editProfileButton:
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;

            case R.id.QRButton:
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //hide keyboard when any /fragment of this class has been detached
        // showSoftwareKeyboard(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileData();
    }

    protected void showSoftwareKeyboard(boolean showKeyboard){
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), showKeyboard ? InputMethodManager.SHOW_FORCED : InputMethodManager.HIDE_NOT_ALWAYS);

        }catch (NullPointerException e){
            Log.d("TAB1", "Keybaord " + e.getMessage());
        }

    }



}
