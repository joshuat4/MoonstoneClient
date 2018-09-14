package com.moonstone.ezmaps_app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Tab3Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View fragmentLayout;
    private ContactRecyclerViewAdapter adapter;
    private boolean notFirstTime = false;

    private EditText contactFiler;
    private Button newContactButton;
    public static ProgressBar contactsLoading;

    //Arrays needed for recyclerView
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_three, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        contactFiler = fragmentLayout.findViewById(R.id.contactFilter);
        newContactButton = fragmentLayout.findViewById(R.id.contactAddButton);
        contactsLoading = fragmentLayout.findViewById(R.id.contactsLoading);


        profilePics = new ArrayList<>() ;
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();

        //Filter code
        contactFiler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        loadDataFromFirebase();

//        if(!notFirstTime){
//            final String Uid = mAuth.getUid();
//            Log.d("HERE", "please don't run");
//            db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    if(documentSnapshot.exists()){
//                        //Do nothing
//                    }
//                    else{
//                        final ArrayList<String> contacts = (ArrayList<String>) documentSnapshot.get("contacts");
//                        for (String contact : contacts){
//                            db.collection("users").document(contact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    profilePics.add(documentSnapshot.get("profilePic").toString());
//                                    emails.add(documentSnapshot.get("email").toString());
//                                    names.add(documentSnapshot.get("name").toString());
//                                    ids.add(documentSnapshot.getId());
//                                    //adapter.refreshData();
//                                    if(names.size() == contacts.size()){
//                                        contactsLoading.setVisibility(View.GONE);
//                                        initRecyclerView();
//                                        notFirstTime = true;
//                                    }
//                                }
//                            });
//                        }
//                    }
//
//                }
//            });
//        }


        //Set up add new contacts button
        newContactButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                newContact();
            }
        });


        return fragmentLayout;
    }

    //(Context context, ArrayList<String> contactNames, ArrayList<String> profilePics, ArrayList<String> ids, ArrayList<String> emails){

    //Sets up the recycler view
    private void initRecyclerView(){
        RecyclerView recyclerView =  fragmentLayout.findViewById(R.id.contactRecyclerView);
        Log.d("HERE", names.toString());
        adapter = new ContactRecyclerViewAdapter(getActivity(), names, profilePics, ids, emails);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void filter(String text){

        //Filtered arrays
        ArrayList<String> fprofilePics = new ArrayList<>() ;
        ArrayList<String> fids = new ArrayList<>();
        ArrayList<String> femails = new ArrayList<>();
        ArrayList<String> fnames = new ArrayList<>();

        int counter = 0;

        for(String name : names){
            if(name.toLowerCase().contains(text.toLowerCase())){
                fprofilePics.add(profilePics.get(counter));
                fids.add(ids.get(counter));
                femails.add(emails.get(counter));
                fnames.add(names.get(counter));
            }
            counter += 1;
        }
        adapter.filterList(fnames, fprofilePics, fids, femails);
    }

    private void newContact(){
        startActivity(new Intent(getActivity(), NewContactSearch.class));
    }

    private void refresh(){
        Log.d("HERE", "refresh");
//        contactsLoading.setVisibility(View.VISIBLE);
//        adapter.clear();

//        adapter.refreshData();
        loadDataFromFirebase();
    }


    @Override
    public void onResume(){
        refresh();
        super.onResume();
        //other stuff
    }

    private void loadDataFromFirebase() {
        contactsLoading.setVisibility(View.VISIBLE);
        if (names.size() > 0) {
            profilePics.clear();
            ids.clear();
            emails.clear();
        }
        final String Uid = mAuth.getUid();
        Log.d("HERE", "please don't run");
        db.collection("users").document(Uid).collection("contacts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
                            final String docId = querySnapshot.getId();
                            db.collection("users").document(docId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                            DocumentSnapshot doc = task2.getResult();
                                            names.add(doc.getString("name"));
                                            profilePics.add(doc.getString("profilePic"));
                                            ids.add(docId);
                                            emails.add(doc.getString("profilePic"));
                                        }
                                    });
                        }
                        contactsLoading.setVisibility(View.GONE);
                        initRecyclerView();
                        notFirstTime = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_SHORT).show();
                        Log.d("FAILURE IN CONTACTS", e.getMessage());
                    }
                });
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            refresh();
//        }
//    }
}