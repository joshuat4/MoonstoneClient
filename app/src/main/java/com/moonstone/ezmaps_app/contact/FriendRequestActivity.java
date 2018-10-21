package com.moonstone.ezmaps_app.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FriendRequestsRecyclerViewAdapter adapter;
    private ActionBar actionbar;
    public ProgressBar requestsLoading;
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;
    private Intent intent;
    private boolean requestsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        requestsLoading = findViewById(R.id.requestLoading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("Pending Friend Requests");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });


        profilePics = new ArrayList<>();
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadRequestsFromDB();

    }


    private void loadRequestsFromDB() {

        final String Uid = mAuth.getUid();
        final DocumentReference docRef = db.collection("users").document(Uid);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("FriendRequestsActivity", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("FriendRequestsActivity", "Current data: " + snapshot.getData());

                    names.clear();
                    emails.clear();
                    ids.clear();
                    profilePics.clear();

                    try {
                        final ArrayList<String> requests = (ArrayList<String>) snapshot.get("requests");

                        Log.d("ChooseContactsActivity", "REQUESTS: " + requests);

                        if (!requests.isEmpty()) {
                            for (String request : requests) {
                                db.collection("users").document(request).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        //add things that aren't already in the view
                                        if (!ids.contains(documentSnapshot.getId())) {
                                            profilePics.add(documentSnapshot.get("profilePic").toString());
                                            emails.add(documentSnapshot.get("email").toString());
                                            names.add(documentSnapshot.get("name").toString());
                                            ids.add(documentSnapshot.getId());

                                        }

                                        //remove things that are in the view, but dropped from the server
                                        ArrayList<String> removal = new ArrayList<>();
                                        int index;
                                        for (int item = 0; item < ids.size(); item++) {
                                            if (!requests.contains(ids.get(item))) {
                                                removal.add(ids.get(item));
                                            }
                                        }
                                        for (String s : removal) {
                                            index = ids.indexOf(s);
                                            names.remove(index);
                                            emails.remove(index);
                                            ids.remove(index);
                                            profilePics.remove(index);
                                        }

                                        if (names.size() == requests.size()) {
                                            Log.d("FriendRequestsActivity", "second list num: " + names.size());
                                            Log.d("FriendRequestsActivity", "contacts size: " + requests.size());
                                            Log.d("FriendRequestsActivity", "requests available: init recycler view: ");
                                            initRecyclerView();
                                            requestsAvailable = true;

                                        }
                                    }
                                });
                            }

                        } else {
                            requestsAvailable = false;
                            Log.d("FriendRequestsActivity", "requests NOT available: init recycler view: ");
                            initRecyclerView();
                        }

                    } catch (NullPointerException n) {
                        requestsAvailable = false;

                    }

                } else {
                    Log.d("FriendRequestsActivity", "Current data: null");
                }
            }
        });
    }

    //Sets up the recycler view
    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.requestRecyclerView);

        Log.d("ChooseContactsActivity", "Initialise recycler view: " + names.toString());

        adapter = new FriendRequestsRecyclerViewAdapter(FriendRequestActivity.this, names, profilePics, ids, emails, db, mAuth);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendRequestActivity.this));

        requestsLoading.setVisibility(View.GONE);
    }


}
