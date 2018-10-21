package com.moonstone.ezmaps_app.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.qrcode.ScanBarcodeActivity;

import java.util.ArrayList;
import java.util.List;

public class NewContactSearchActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FindRecyclerViewAdapter adapter;

    private EditText filterSearch;
    public ProgressBar findContactsLoading;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private Button searchButton;
    private FloatingActionButton addQRButton;

    private ImageButton clearButton;

    //Arrays needed for recyclerView
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;

    private TextWatcher textWatcher;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_find_new_contacts);
        filterSearch = findViewById(R.id.filterAllContacts);
        findContactsLoading = findViewById(R.id.findContactsLoading);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        toolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.close_dark);
        actionbar.setTitle("Add Contacts");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });


        profilePics = new ArrayList<>() ;
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        searchButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                search(v);
            }
        });


        filterSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        search(v);
                        return true;
                    }
                }
                return false;
            }
        });

        clearButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                filterSearch.getText().clear();
                clearButton.setVisibility(View.GONE);
            }
        });




        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    clearButton.setVisibility(View.GONE);
                }else{
                    clearButton.setVisibility(View.VISIBLE);
                }

            }
        };

    }



    @Override
    public void onResume(){
        super.onResume();
        filterSearch.addTextChangedListener(textWatcher);
    }

    public void search(View v){
        touchAPlace(v);
        findContactsLoading.setVisibility(View.VISIBLE);
        searchForContacts(filterSearch.getText().toString().trim());
    }



    private boolean compareContacts(String text, String against){

        if(against.toUpperCase().contains(text.toUpperCase())){

            Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " SUCCESS");

            return true;
        }


        Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " FAILED");

        return false;
    }


    private void searchForContacts(final String inputText){
        profilePics.clear();
        names.clear();
        emails.clear();
        ids.clear();

        Log.d("Add Contacts", "Searching for contacts: " + inputText);

        Task<QuerySnapshot> d = db.collection("users").get();
        d.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();

                for (DocumentSnapshot doc : list) {
                    if(!doc.getId().equals(mAuth.getUid())){
                        Log.d("AAAA", doc.getId());
                        // Check for name
                        String name = doc.get("name").toString();
                        String email = doc.get("email").toString();

                        if(compareContacts(inputText, name) ||
                                compareContacts(inputText, email)){

                            //add things that aren't already in the view
                            if (!ids.contains(doc.getId())) {
                                profilePics.add(doc.get("profilePic").toString());
                                emails.add(doc.get("email").toString());
                                names.add(doc.get("name").toString());
                                ids.add(doc.getId());

                            }
                        }

                    }
                }

                Log.d("Add Contacts", "Contacts found: " + names.size());

                final String Uid = mAuth.getUid();
                db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> contacts = (ArrayList<String>) documentSnapshot.get("contacts");
                        ArrayList<String> pending = (ArrayList<String>) documentSnapshot.get("pendingRequests");

                        Log.d("Add Contacts", "user's contacts received: " + contacts);
                        Log.d("Add Contacts", "Initialise Recycler view");
                        initRecyclerView(contacts, pending);
                        findContactsLoading.setVisibility(View.GONE);
                    }
                });
            }


        });


    }

    private void touchAPlace(View view){
        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        // List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );

        // Dispatch touch event to view
        view.dispatchTouchEvent(motionEvent);
    }


    private void initRecyclerView(ArrayList<String> contacts, ArrayList<String> pending){
        RecyclerView recyclerView =  findViewById(R.id.findRecyclerView);
        adapter = new FindRecyclerViewAdapter(this, names, profilePics, ids, emails, contacts, pending);
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}
