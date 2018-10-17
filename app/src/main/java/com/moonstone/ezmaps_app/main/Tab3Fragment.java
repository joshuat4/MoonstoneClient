package com.moonstone.ezmaps_app.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.contact.ContactRecyclerViewAdapter;
import com.moonstone.ezmaps_app.contact.GroupchatRecyclerViewAdapter;
import com.moonstone.ezmaps_app.contact.NewContactSearchActivity;
import com.moonstone.ezmaps_app.contact.RequestsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab3Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View fragmentLayout;
    private ContactRecyclerViewAdapter adapter;
    private RequestsRecyclerViewAdapter requestAdapter;
    private GroupchatRecyclerViewAdapter groupchatAdapter;
    private boolean contactsAvailable = false;
    private static boolean fromNav;

    private EditText contactFilter;
    private Button newContactButton;
    public ProgressBar contactsLoading;
    private ImageButton clearButton;
    private CheckBox select;
    static public Boolean checked;

    //Arrays needed for recyclerView
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;

    private ArrayList<String> reqProfilePics;
    private ArrayList<String> reqNames;
    private ArrayList<String> reqIds;

    private ArrayList<ArrayList<String>> groupchatNames;
    private ArrayList<ArrayList<String>> groupchatUserIds;
    private ArrayList<String> groupchatIds;
    private ArrayList<String> newGroupIds;


    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayList<String> requests = new ArrayList<>();
    private ArrayList<String> groupchats = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_three, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        checked = false;

        contactFilter = fragmentLayout.findViewById(R.id.contactFilter);
        newContactButton = fragmentLayout.findViewById(R.id.contactAddButton);
        contactsLoading = fragmentLayout.findViewById(R.id.contactsLoading);
        select = (CheckBox) fragmentLayout.findViewById(R.id.Select);


        profilePics = new ArrayList<>() ;
        ids = new ArrayList<>();
        emails = new ArrayList<>();
        names = new ArrayList<>();

        reqNames = new ArrayList<>();
        reqIds = new ArrayList<>();
        reqProfilePics = new ArrayList<>();

        groupchatUserIds = new ArrayList<>();
        groupchatNames = new ArrayList<>();
        groupchatIds = new ArrayList<>();

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

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the view now checked?
                checked = ((CheckBox) view).isChecked();
                if (checked) {
                    newContactButton.setText("Create Group Chat");
                } else {
                    newContactButton.setText("Add");
                    adapter.clearSelected();
                }
            }
        });


        //Set up add new contacts button, make it so that it can go to the add contacts screen,
        // or make a new group chat
        newContactButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!checked)
                {
                    newContact();
                } else {
                    select.setChecked(false);
                    checked = false;
                    String thisUserId = mAuth.getUid();
                    newGroupIds = new ArrayList<String>(adapter.getSelectedIds());
                    newGroupIds.add(thisUserId);
                    //make the group chat
                    Map<String, Object> data = new HashMap<>();
                    data.put("ids", newGroupIds);

                    //make the groupchat
                    db.collection("groupchats").add(data).addOnCompleteListener(
                            new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    final String groupChatId = task.getResult().getId();
                                    Log.d("groupchat", "groupchat id: " + groupChatId);

                                    //add the group chat to all included party's contacts
                                    for(int i = 0; i<newGroupIds.size(); i++){
                                        DocumentReference docRef = db
                                                .collection("users")
                                                .document(newGroupIds.get(i));
                                        docRef.update(
                                                "groupchats", FieldValue
                                                        .arrayUnion(groupChatId));
                                        docRef.collection("groupchats")
                                                .document(groupChatId).update(
                                                "unread", "0");
                                    }
                                }
                            });
                }

            }
        });

        contactFilter.setSelected(false);

        return fragmentLayout;
    }


    private void loadContactsFromDB(){

        final String Uid = mAuth.getUid();
        final DocumentReference docRef = db.collection("users").document(Uid);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAB3", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAB3", "Current data: " + snapshot.getData());
                    names.clear();
                    emails.clear();
                    ids.clear();
                    profilePics.clear();
                    reqProfilePics.clear();
                    reqNames.clear();
                    reqIds.clear();
                    contacts.clear();
                    requests.clear();

                    try{
                        contacts = (ArrayList<String>) snapshot.get("contacts");
                        requests = (ArrayList<String>) snapshot.get("requests");
                        Log.d("TAB3", "CONTACTS: " + contacts);

                        if(!requests.isEmpty()) {

                            for (String request : requests) {
                                db.collection("users").document(request).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        if(!reqIds.contains(documentSnapshot.getId())) {
                                            reqProfilePics.add(documentSnapshot.get("profilePic").toString());
                                            reqNames.add(documentSnapshot.get("name").toString());
                                            reqIds.add(documentSnapshot.getId());
                                        }

                                        Log.d("qqqqq", reqNames.toString());

                                        //Might cause a race condition
                                        if (reqNames.size() == requests.size()) {
                                            Log.d("duplication", "reqNames: " + reqNames.toString());
                                            requestAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                        if(!contacts.isEmpty()){

                            for (String contact : contacts){
                                db.collection("users").document(contact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        if(!ids.contains(documentSnapshot.getId())) {
                                            profilePics.add(documentSnapshot.get("profilePic").toString());
                                            emails.add(documentSnapshot.get("email").toString());
                                            names.add(documentSnapshot.get("name").toString());
                                            ids.add(documentSnapshot.getId());
                                        }

                                        if(names.size() == contacts.size()){
                                            Log.d("TAB3", "second list num: " + names.size());
                                            Log.d("TAB3", "contacts size: " + contacts.size());
                                            Log.d("TAB3", "contacts available: init recycler view: ");
                                            Log.d("duplication", "contactNames: " + names.toString());

                                            adapter.notifyDataSetChanged();
                                            contactsAvailable = true;

                                        }
                                    }
                                });
                            }

                        }else{
                            contactsAvailable = false;
                            Log.d("TAB3", "contacts NOT available: init recycler view: ");
                            adapter.notifyDataSetChanged();
                        }

                    } catch (NullPointerException n){
                        contactsAvailable = false;

                    }

                } else {
                    Log.d("TAB3", "Current data: null");
                }
            }
        });
    }

    //Sets up the recycler view
    private void initRecyclerView(){

        RecyclerView recyclerView =  fragmentLayout.findViewById(R.id.contactRecyclerView);

        Log.d("TAB3", "Initialise recycler view: " + names.toString());

        adapter = new ContactRecyclerViewAdapter(getActivity(), getActivity(), names, profilePics, ids, emails);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        contactFilter.setSelected(false);
        contactsLoading.setVisibility(View.INVISIBLE);
    }

    private void initRequestsRecyclerView(){
        //contact requests recycler view
        RecyclerView requestRecyclerView = fragmentLayout.findViewById(R.id.requestRecyclerView);

        Log.d("aaaaa", "is this here");

        requestAdapter = new RequestsRecyclerViewAdapter(getActivity(), reqNames, reqProfilePics, reqIds, db, mAuth);
        requestRecyclerView.setAdapter(requestAdapter);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initGroupchatRecyclerView() {
        //groupchats recycler view
        RecyclerView groupchatRecyclerView = fragmentLayout.findViewById(R.id.groupchatRecyclerView);
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
        Intent intent = new Intent(getActivity(), NewContactSearchActivity.class);
        startActivityForResult(intent, 3);
    }

    @Override
    public void onResume(){
        super.onResume();
        initRecyclerView();
        initRequestsRecyclerView();
        loadContactsFromDB();
        Log.d("duplication", "onResume called");
        adapter.notifyDataSetChanged();
        requestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 3) && (resultCode == Activity.RESULT_OK)){
            refreshFragment();

        }
    }

    public void refreshFragment(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public static void setFromNav(boolean update){
        fromNav = update;
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
