package com.moonstone.ezmaps_app.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
import com.moonstone.ezmaps_app.ezdirection.EZDirectionActivity;
import com.moonstone.ezmaps_app.qrcode.ScanBarcodeActivity;
import com.moonstone.ezmaps_app.contact.RecView;
import com.moonstone.ezmaps_app.utilities.IntCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab3Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private View fragmentLayout;
    private View blocker;
    private ContactRecyclerViewAdapter adapter; //contacts recycler view
    private RequestsRecyclerViewAdapter requestAdapter; //requests recview
    private GroupchatRecyclerViewAdapter groupchatAdapter; //groupchat recview

    private boolean contactsAvailable = false;
    private static boolean fromNav;
    int numMembers;
    int groupNumber; // number of groups

    private EditText contactFilter; //filters contacts
    private com.getbase.floatingactionbutton.FloatingActionButton  newContactButton;
    private com.getbase.floatingactionbutton.FloatingActionButton  addQRButton;
    public ProgressBar contactsLoading;
    private ImageButton clearButton;
    private CheckBox select; //checkbox for selecting multiple contacts
    static public Boolean checked; //whether the above checkbox is checked

    //Arrays needed for recyclerView
    private ArrayList<String> profilePics;
    private ArrayList<String> ids;
    private ArrayList<String> emails;
    private ArrayList<String> names;

    private ArrayList<String> reqProfilePics;
    private ArrayList<String> reqNames;
    private ArrayList<String> reqIds;
    private ArrayList<String> respondedRequests = new ArrayList<>();

    private ArrayList<ArrayList<String>> groupchatNames;
    private ArrayList<ArrayList<String>> groupchatUserIds; //ids of groupchat's members
    private ArrayList<String> groupchatIds; //firestore doc id of the groupchat
    private ArrayList<String> newGroupIds; //


    private LinearLayout newRequestHeader;
    private LinearLayout contactsHeader;
    private boolean requestsAvailable = false;

    private ArrayList<String> contacts = new ArrayList<>(); //contacts pulled from db
    private ArrayList<String> requests = new ArrayList<>(); //requests pulled from db
    private ArrayList<String> groupchats = new ArrayList<>(); //groupchats pulled from db

    //whether a recyclerview is loading/loaded
    private ArrayList<Boolean> loaded = new ArrayList<>(); //arraylist of the recycler views and contains
    // the status of whether they're loaded or not
    private ArrayList<Boolean> loading = new ArrayList<>();//arraylist of the rec views and contains status of whether they'e loading or not

    private TextWatcher textWatcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentLayout = inflater.inflate(R.layout.fragment_three, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        checked = false;

        contactFilter = fragmentLayout.findViewById(R.id.contactFilter);
        contactsLoading = fragmentLayout.findViewById(R.id.contactsLoading);
        select = (CheckBox) fragmentLayout.findViewById(R.id.select);
        blocker = fragmentLayout.findViewById(R.id.blocker);

        //set up loaded and loading
        loaded.clear();
        loading.clear();
        for(int e = 0; e < 3; e++){
            loaded.add(false);
            loading.add(false);
        }

        contactsHeader = fragmentLayout.findViewById(R.id.contactsHeader);
        newRequestHeader = fragmentLayout.findViewById(R.id.newRequestsHeader);

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
                Log.d("CLEARBUTTON", "GONE");
            }
        });



        //for filtering contacts based on text input
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

                // Check if there is contacts available before filtering
                if(contactsAvailable){
                    filter(s.toString());
                }

            }
        };


        final FloatingActionsMenu mainAddButton =
                (FloatingActionsMenu) fragmentLayout.findViewById(R.id.mainAddButton);

        addQRButton =
                (com.getbase.floatingactionbutton.FloatingActionButton) fragmentLayout.findViewById(R.id.addQR);
        newContactButton =
                (com.getbase.floatingactionbutton.FloatingActionButton) fragmentLayout.findViewById(R.id.addContact);


        addQRButton.setIcon(R.drawable.qr_example);
        addQRButton.setTitle("Add via QR");
        addQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Add Contacts through qr","qr scan cam initiated");
                Intent intent = new Intent(view.getContext() , ScanBarcodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        newContactButton.setIcon(R.drawable.add_contact);
        newContactButton.setTitle("Add Contacts");
        //Set up add new contacts button
        newContactButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                // if not checked, just start trying to add a contact
                if(!checked)
                {
                    newContact();
                } else {
                    //if checked, create a new groupchat
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

                                    //add the group chat to all included party's groupchats field in their user document on firestore
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

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the view now checked?
                checked = ((CheckBox) view).isChecked();
                if (checked) {
                    newContactButton.setTitle("Create Group Chat");
                } else {
                    newContactButton.setTitle("Add Contacts");
                    adapter.clearSelected();
                }
            }
        });

        contactFilter.setSelected(false);
        //initialise the recycler views
        initRecyclerView();
        initRequestsRecyclerView();
        initGroupchatRecyclerView();
        return fragmentLayout;
    }


    private void loadContactsFromDB(){
        contactsLoading.setVisibility(View.VISIBLE);
        contacts = new ArrayList<>();
        requests = new ArrayList<>();
        groupchats = new ArrayList<>();


        final String Uid = mAuth.getUid();
        final DocumentReference docRef = db.collection("users").document(Uid);
        //update recycler views everytime the user doc changes
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                Log.d("loaded", "blocker enabled");
                //block selection of items while loading
                blocker.setClickable(true);
                //if loading in already, wait to load more stuff for 5 seconds
                if (loading.contains(true)) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadContactsFromDB();
                        }
                    }, 5000);
                //if not already loading in, load in
                } else {
                    if (e != null) {
                        Log.w("TAB3", "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        reloading();
                        Log.d("TAB3", "Current data: " + snapshot.getData());
                        names.clear();
                        emails.clear();
                        ids.clear();
                        profilePics.clear();
                        if(contacts != null){
                            contacts.clear();
                        }

                        reqProfilePics.clear();
                        reqNames.clear();
                        reqIds.clear();
                        if(requests != null){
                            requests.clear();
                        }

                        groupchatNames.clear();
                        groupchatIds.clear();
                        groupchatUserIds.clear();
                        if(groupchats != null){
                            groupchats.clear();
                        }

                        Log.d("loaded", "322");

                        try {
                            Log.d("loaded", "325");
                            contacts = new ArrayList<>((ArrayList<String>) snapshot.get("contacts"));
                            requests = new ArrayList<>((ArrayList<String>) snapshot.get("requests"));
                            groupchats = new ArrayList<>((ArrayList<String>) snapshot.get("groupchats"));
                            if(contacts == null){
                                updateLoaded(RecView.CONTACTS);
                            }
                            if(requests == null){
                                updateLoaded(RecView.REQUESTS);
                            }
                            if(groupchats == null){
                                updateLoaded(RecView.GROUPCHATS);
                            }
                            Log.d("TAB3", "CONTACTS: " + contacts);
                            Log.d("loaded", "340");
                            Log.d("TAB3", "GROUPCHATS loading 270: " + loading.get(RecView.GROUPCHATS.getNumVal()).toString());

                            //if not currently loading in requests, load in requests
                            if (!loading.get(RecView.REQUESTS.getNumVal())) {
                                Log.d("loaded", "345");
                                loading.set(RecView.REQUESTS.getNumVal(), true);
                                if (!requests.isEmpty()) {
                                    if (requests.size() == 0) {
                                        updateLoaded(RecView.REQUESTS);
                                    }
                                    for (String request : requests) {
                                        //get the request's user from db
                                        db.collection("users").document(request).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                //add things that aren't already in the view
                                                if (!reqIds.contains(documentSnapshot.getId())) {
                                                    reqProfilePics.add(documentSnapshot.get("profilePic").toString());
                                                    reqNames.add(documentSnapshot.get("name").toString());
                                                    reqIds.add(documentSnapshot.getId());
                                                }

                                                //remove things that are in the view, but dropped from the server
                                                ArrayList<String> removal = new ArrayList<>();
                                                int index;
                                                for (int item = 0; item < reqIds.size(); item++) {
                                                    if (!requests.contains(reqIds.get(item))) {
                                                        removal.add(reqIds.get(item));
                                                    }
                                                }

                                                for (String s : removal) {
                                                    index = reqIds.indexOf(s);
                                                    reqNames.remove(index);
                                                    reqIds.remove(index);
                                                    reqProfilePics.remove(index);
                                                }


                                                //Might cause a race condition
                                                if (reqNames.size() == requests.size()) {
                                                    //remove requests that have already been responded to
                                                    for (String s : respondedRequests) {
                                                        if(reqIds.contains(s)){
                                                            index = reqIds.indexOf(s);
                                                            reqNames.remove(index);
                                                            reqIds.remove(index);
                                                            reqProfilePics.remove(index);
                                                        }
                                                    }
                                                    Log.d("duplication", "reqNames: " + reqNames.toString());
                                                    requestAdapter.notifyDataSetChanged();
                                                    updateLoaded(RecView.REQUESTS);
                                                    respondedRequests.clear();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    updateLoaded(RecView.REQUESTS);
                                }
                            }

                            Log.d("loaded", "390");
                            Log.d("TAB3", "GROUPCHATS loading 318: " + loading.get(RecView.GROUPCHATS.getNumVal()).toString());

                            //if not currently loading in contacts, load in contacts
                            if (!loading.get(RecView.CONTACTS.getNumVal())) {
                                loading.set(RecView.CONTACTS.getNumVal(), true);
                                Log.d("loaded", "398");
                                if (!contacts.isEmpty()) {
                                    if (contacts.size() == 0) {
                                        updateLoaded(RecView.CONTACTS);
                                    }
                                    for (String contact : contacts) {
                                        //load in contact's user from the db
                                        db.collection("users").document(contact).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                                    if (!contacts.contains(ids.get(item))) {
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

                                                if (names.size() == contacts.size()) {
                                                    Log.d("TAB3", "second list num: " + names.size());
                                                    Log.d("TAB3", "contacts size: " + contacts.size());
                                                    Log.d("TAB3", "contacts available: init recycler view: ");
                                                    Log.d("duplication", "contactNames: " + names.toString());

                                                    adapter.notifyDataSetChanged();
                                                    if (contacts.size() == 0) {
                                                        updateLoaded(RecView.CONTACTS);
                                                        loading.set(RecView.CONTACTS.getNumVal(), false);
                                                    }
                                                    contactsAvailable = true;

                                                }
                                            }
                                        });
                                    }
                                    Log.d("loaded", "444");
                                } else {
                                    Log.d("loaded", "450");
                                    contactsAvailable = false;
                                    Log.d("TAB3", "contacts NOT available: init recycler view: ");
                                    adapter.notifyDataSetChanged();
                                    loading.set(RecView.CONTACTS.getNumVal(), false);
                                    updateLoaded(RecView.CONTACTS);
                                }
                            }

                            //Now load in groupchats
                            Log.d("TAB3", "GROUPCHATS: " + groupchats);
                            Log.d("TAB3", "GROUPCHATS loading: " + loading.get(RecView.GROUPCHATS.getNumVal()).toString());
                            //if not currently loading in groupchats, load in groupchats
                            if (!loading.get(RecView.GROUPCHATS.getNumVal())) {
                                loading.set(RecView.GROUPCHATS.getNumVal(), true);
                                if (!groupchats.isEmpty()) {
                                    //add things that aren't already in the view
                                    if (groupchats.size() == 0) {
                                        updateLoaded(RecView.GROUPCHATS);
                                    }
                                    for (int groupNum = 0; groupNum < groupchats.size(); groupNum++) {
                                        if (!groupchatIds.contains(groupchats.get(groupNum))) {
                                            final String group = groupchats.get(groupNum);
                                            groupchatIds.add(group);
                                        }
                                    }

                                    //remove things that are in the view, but dropped from the server
                                    ArrayList<String> removal = new ArrayList<>();
                                    int index;
                                    for (int item = 0; item < groupchatIds.size(); item++) {
                                        if (!groupchats.contains(groupchatIds.get(item))) {
                                            removal.add(groupchatIds.get(item));
                                        }
                                    }
                                    for (String s : removal) {
                                        index = groupchatIds.indexOf(s);
                                        groupchatNames.remove(index);
                                        groupchatIds.remove(index);
                                        groupchatUserIds.remove(index);
                                    }

                                    groupNumber = 0;
                                    for (String group : groupchatIds) {
                                        Log.d("groupchatDupes", "load members called: " + groupchatIds.toString());
                                        loadMembers(group);
                                    }
                                    if (groupchatIds.size() == 0) {
                                        updateLoaded(RecView.GROUPCHATS);
                                    }
                                } else {
                                    updateLoaded(RecView.GROUPCHATS);
                                }
                            }
                        } catch (NullPointerException n) {
                            contactsAvailable = false;

                        }

                    } else {
                        Log.d("TAB3", "Current data: null");
                    }
                }
            }
        });
    }


    //Sets up the recycler view
    private void initRecyclerView(){

        RecyclerView recyclerView =  fragmentLayout.findViewById(R.id.contactRecyclerView);

        Log.d("TAB3", "Initialise recycler view: " + names.toString());

        adapter = new ContactRecyclerViewAdapter(this, getActivity(), getActivity(), names, profilePics, ids, emails);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contactsLoading.setVisibility(View.INVISIBLE);


        contactFilter.setSelected(false);
        contactsLoading.setVisibility(View.INVISIBLE);
    }

    private void initRequestsRecyclerView(){
        //contact requests recycler view
        RecyclerView requestRecyclerView = fragmentLayout.findViewById(R.id.requestRecyclerView);

        requestAdapter = new RequestsRecyclerViewAdapter(this, getActivity(), reqNames, reqProfilePics, reqIds, db, mAuth);
        requestRecyclerView.setAdapter(requestAdapter);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void initGroupchatRecyclerView() {
        //groupchats recycler view
        RecyclerView groupchatRecyclerView = fragmentLayout.findViewById(R.id.groupchatRecyclerView);

        groupchatAdapter = new GroupchatRecyclerViewAdapter(this, getActivity(), groupchatIds, groupchatNames, groupchatUserIds, db, mAuth);
        groupchatRecyclerView.setAdapter(groupchatAdapter);
        groupchatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //filter out contacts based on input text
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

    //load in all the members of a groupchat and their names
    private void loadMembers(final String group){
        db.collection("groupchats").document(group).get()
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(groupchatNames.size() < groupchatIds.size()){
                                    final int groupNum = groupNumber;
                                    Log.d("groupchat", "current group: " + group);
                                    Log.d("groupchat", "groupNumber: " + groupNumber);
                                    Log.d("groupchat", "groupNumr: " + groupNum);
                                    Log.d("groupchat", "groupvhatUids.size(): " + groupchatUserIds.size());
                                    ArrayList<String> ids = (ArrayList<String>) documentSnapshot.get("ids");
                                    Log.d("groupchatDupes", "adding ids: " + groupchatUserIds.toString());
                                    groupchatUserIds.add(new ArrayList<>(ids));
                                    Log.d("groupchatDupes", "add to groupchat names called");
                                    groupchatNames.add(new ArrayList<String>());

                                    Log.d("groupchat", "current ids: " + groupchatUserIds.toString());
                                    //get the names of all the groupchat members
                                    final IntCounter numMembers = new IntCounter();
                                    for(int i = 0; i<groupchatUserIds.get(groupNum).size(); i++) {
                                        //only add the name if it's not the current user's name,
                                        //so that the groupchat's name doesn't end up displaying as containing the user's name
                                        if(!groupchatUserIds.get(groupNum).get(i).equals(mAuth.getUid())) {
                                            db.collection("users").document(groupchatUserIds.get(groupNum).get(i)).get()
                                                    .addOnSuccessListener(
                                                            new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    numMembers.number++;
                                                                    groupchatNames.get(groupNum).add(documentSnapshot.getString("name"));
                                                                    Log.d("groupchat", "current names: " + documentSnapshot.getString("name"));
                                                                    Log.d("groupchat", "current group names: " + groupchatNames.get(groupNum).toString());
                                                                    Log.d("groupchatDupes", "all group names: " + groupchatNames.toString());
                                                                    Log.d("groupchat", "current numMembers: " + numMembers);
                                                                    Log.d("groupchat", "current Uids size: " + groupchatUserIds.get(groupNum).size());
                                                                    Log.d("groupchatDupes", "nummems +1 = "+(numMembers.number+1));
                                                                    Log.d("groupchatDupes", "groupchatUids = "+groupchatUserIds.get(groupNum).size());
                                                                    if ((numMembers.number + 1) == groupchatUserIds.get(groupNum).size()) {
                                                                        Log.d("groupchat", "how many groupchatNames: " + groupchatNames.size());
                                                                        Log.d("groupchat", "groupchatNames: " + groupchatNames.toString());
                                                                        Log.d("groupchat", "groupchats size: " + groupchats.size() + ", " + groupchats.toString());
                                                                        Log.d("groupchat", "groupchats available: init recycler view: ");
                                                                        Log.d("groupchat", "GROUPCHATS 2 : " + groupchats);
                                                                        Log.d("groupchatDupes", "LOADED!");
                                                                        groupchatAdapter.notifyDataSetChanged();
                                                                        updateLoaded(RecView.GROUPCHATS);
                                                                    }
                                                                }
                                                            });
                                        }
                                    }

                                    Log.d("groupchatDupes", "0 field nummems +1 = "+(numMembers.number+1));
                                    Log.d("groupchatDupes", "0 field groupchatUids = "+groupchatUserIds.get(groupNum).size());
                                    if(numMembers.number == groupchatUserIds.get(groupNum).size()){
                                        Log.d("TAB3", "how many groupchatNames: " + groupchatNames.size());
                                        Log.d("TAB3", "groupchats size: " + groupchats.size() + ", " + groupchats.toString());
                                        Log.d("TAB3", "groupchats available: init recycler view: ");
                                        Log.d("TAB3", "GROUPCHATS 2 : " + groupchats);
                                        groupchatAdapter.notifyDataSetChanged();
                                        updateLoaded(RecView.GROUPCHATS);
                                    }
                                    groupNumber ++;
                                }
                            }
                        });
    }

    @Override
    public void onResume(){
        super.onResume();
        contactFilter.addTextChangedListener(textWatcher);
        loadContactsFromDB();
        Log.d("duplication", "onResume called");
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

    //loading logic, updated from onBindViewHolder for each recycler view: until they're all
    // finished loading, block interaction with elements, and display loading wheel
    public void updateLoaded(RecView recView){
        Boolean ready = false;

        loaded.set(recView.getNumVal(), true);
        loading.set(recView.getNumVal(), false);

        if(!loaded.contains(false)){
            ready = true;
        }

        Log.d("loaded", "ready: " + ready.toString());
        Log.d("loaded", "loaded: " + loaded.toString());
        if(ready == false){
            contactsLoading.setVisibility(View.VISIBLE);
            //unblock selection
        } else {
            contactsLoading.setVisibility(View.INVISIBLE);
            blocker.setClickable(false);
            //block selection
        }
    }

    private void reloading() {
        for(int e = 0; e<loaded.size(); e++){
            loaded.set(e,false);
        }
        contactsLoading.setVisibility(View.VISIBLE);
    }

    public void addToRespondedRequests(String id){
        //add things that aren't already in the list
        if (!respondedRequests.contains(id)) {
            respondedRequests.add(id);
        }
    }
    
}
