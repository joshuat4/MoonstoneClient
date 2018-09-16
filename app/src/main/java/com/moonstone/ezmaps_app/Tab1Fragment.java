package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import android.util.Log;

import butterknife.ButterKnife;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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

        _test = (ImageView) view.findViewById(R.id.test);

        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
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
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
                break;
        }
    }


}
