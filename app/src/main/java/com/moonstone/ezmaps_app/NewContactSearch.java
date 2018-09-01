package com.moonstone.ezmaps_app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewContactSearch extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FindRecyclerViewAdapter adapter;

    private Button backButton;
    private EditText filterSearch;

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

        backButton = findViewById(R.id.backButton);
        filterSearch = findViewById(R.id.filterAllContacts);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
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
                    profilePics.add(doc.get("profilePic").toString());
                    emails.add(doc.get("email").toString());
                    names.add(doc.get("name").toString());
                    ids.add(doc.getId());
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

    }

    private void back(){
        finish();
    }

    private void initRecyclerView(ArrayList<String> contacts){
        RecyclerView recyclerView =  findViewById(R.id.findRecyclerView);
        adapter = new FindRecyclerViewAdapter(this, names, profilePics, ids, emails, contacts);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toast.makeText(NewContactSearch.this, Integer.toString(adapter.getItemCount()), Toast.LENGTH_SHORT).show();
    }
}
