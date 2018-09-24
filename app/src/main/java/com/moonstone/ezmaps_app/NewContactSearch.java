package com.moonstone.ezmaps_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moonstone.ezmaps_app.FindRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewContactSearch extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FindRecyclerViewAdapter adapter;

    private EditText filterSearch;
    public static ProgressBar findContactsLoading;
    private Toolbar toolbar;
    private ActionBar actionbar;

    //Arrays needed for recyclerView
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_new_contacts);

        profilePics = new ArrayList<>() ;
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();

        filterSearch = findViewById(R.id.filterAllContacts);
        findContactsLoading = findViewById(R.id.findContactsLoading);


        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Add New Contacts");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Task<QuerySnapshot> d = db.collection("users").get();

        d.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> l = task.getResult().getDocuments();
                //Fill in the necessary arrays
                for (DocumentSnapshot doc : l) {
                    if(doc.getId().equals(mAuth.getUid())){

                    }
                    else{
                        profilePics.add(doc.get("profilePic").toString());
                        emails.add(doc.get("email").toString());
                        names.add(doc.get("name").toString());
                        ids.add(doc.getId());
                    }
                }

                final String Uid = mAuth.getUid();
                db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> contacts = (ArrayList<String>) documentSnapshot.get("contacts");
//                fragmentLayout.findViewById(R.id.contactsLoading).setVisibility(View.GONE);
                        initRecyclerView(contacts);
                    }
                });
            }
        });

        filterSearch.addTextChangedListener(new TextWatcher() {
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

    }

    private void back(){
        finish();
    }

    private void initRecyclerView(ArrayList<String> contacts){
        RecyclerView recyclerView =  findViewById(R.id.findRecyclerView);
        adapter = new FindRecyclerViewAdapter(this, names, profilePics, ids, emails, contacts);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

}
