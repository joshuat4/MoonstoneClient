package com.moonstone.ezmaps_app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.moonstone.ezmaps_app.ContactRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Date;

public class Tab3Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View fragmentLayout;
    private ContactRecyclerViewAdapter adapter;
    private boolean contactsAvailable = false;

    private EditText contactFilter;
    private Button newContactButton;
    public static ProgressBar contactsLoading;
    private ImageButton clearButton;

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

        contactFilter = fragmentLayout.findViewById(R.id.contactFilter);
        newContactButton = fragmentLayout.findViewById(R.id.contactAddButton);
        contactsLoading = fragmentLayout.findViewById(R.id.contactsLoading);

        profilePics = new ArrayList<>() ;
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();

        loadContactsFromDB();

        clearButton = (ImageButton) fragmentLayout.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                contactFilter.getText().clear();
                clearButton.setVisibility(View.GONE);
            }
        });

        //Filter code
        contactFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                clearButton.setVisibility(View.VISIBLE);

                // Check if there is contacts available before filtering
                if(contactsAvailable){
                    filter(s.toString());
                }

            }
        });


        //Set up add new contacts button
        newContactButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                newContact();
            }
        });

        return fragmentLayout;
    }

    private void loadContactsFromDB(){

        contactsLoading.setVisibility(View.VISIBLE);

        final String Uid = mAuth.getUid();
        final DocumentReference docRef = db.collection("users").document(Uid);

        // Checks to see if there are any new updates (if user has new contacts added or deleted)
        docRef.collection("contacts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                final ArrayList<String> contacts = new ArrayList<>();

                //gets all added contacts from the database
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            contacts.add(dc.getDocument().getId());
                            Log.d("contacts", "new contact: " + dc.getDocument().getId());
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }

                //gets all relevant users frrom the users collection
                for (String contact : contacts){
                    db.collection("users").document(contact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            profilePics.add(documentSnapshot.get("profilePic").toString());
                            emails.add(documentSnapshot.get("email").toString());
                            names.add(documentSnapshot.get("name").toString());
                            ids.add(documentSnapshot.getId());

                            if( (names.size() == contacts.size()) && (names.size() > 0)){
                                initRecyclerView();
                            }
                        }
                    });
                }

                contactsAvailable = true;

            }
        });
    }


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

        try {
            adapter.filterList(fnames, fprofilePics, fids, femails);
        }catch (NullPointerException e){
            Log.d("TAB3", "Filter " + e.getMessage());
        }

    }

    private void newContact(){
        startActivity(new Intent(getActivity(), NewContactSearch.class));
    }

    @Override
    public void onResume(){
        super.onResume();
        //other stuff
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }




    /*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
        }
    }
    */
}
