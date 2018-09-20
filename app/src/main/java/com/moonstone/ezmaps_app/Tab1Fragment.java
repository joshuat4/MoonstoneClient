package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import android.support.v4.widget.SwipeRefreshLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab1Fragment extends Fragment implements OnClickListener{

    private Button editProfileButton;
    private Button _QRButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView _nameField;
    private TextView _emailField;
    private CircleImageView _profilePic;

    private static String name;
    private static String email;
    private static String profilePic;

    private ImageView _test;

    SwipeRefreshLayout mSwipeRefreshLayout;

    public static Tab1Fragment newInstance() {
        Tab1Fragment fragment = new Tab1Fragment();
        return fragment;
    }

    public Tab1Fragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        setProfileData();
        name = _nameField.getText().toString();
        email = _emailField.getText().toString();

        return view;
    }


    public void refreshFragment(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
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

    public static String getProfilePic(){
        return profilePic;
    }

    public static void setProfilePic(String s){
        profilePic = s;
    }


    private void setProfileData() {

        final String Uid = mAuth.getUid();

        final DocumentReference docRef = db.collection("users").document(Uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAB1", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    Log.d("TAB1", "Current data: " + snapshot.getData());

                    final String fsName = snapshot.get("name").toString();
                    final String fsEmail = snapshot.get("email").toString();
                    final String fsProfilePic = snapshot.get("profilePic").toString();

                    _nameField.setText(fsName);
                    _emailField.setText(fsEmail);
                    Picasso.get().load(fsProfilePic).into(_profilePic);

                    setName(fsName);
                    setEmail(fsEmail);
                    setProfilePic(fsProfilePic);

                } else {
                    Log.d("TAB1", "Current data: null");
                }
            }
        });

    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.editProfileButton:
                startActivity(new Intent(getActivity(), EditProfile.class));
                break;

            case R.id.QRButton:
                startActivity(new Intent(getActivity(), PopUpActivity.class));
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //hide keyboard when any /fragment of this class has been detached
        // showSoftwareKeyboard(false);
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
