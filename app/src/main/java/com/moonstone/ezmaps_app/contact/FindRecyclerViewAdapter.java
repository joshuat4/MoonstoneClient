package com.moonstone.ezmaps_app.contact;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindRecyclerViewAdapter extends RecyclerView.Adapter<FindRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayList<String> pending = new ArrayList<>();
    private Context mContext;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    final String TAG = "FindRecyclerViewAdaptor";

    public FindRecyclerViewAdapter(Context context, ArrayList<String> contactNames, ArrayList<String> profilePics,
                                   ArrayList<String> ids, ArrayList<String> emails, ArrayList<String> contacts, ArrayList<String> pending){
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.emails = emails;
        this.contacts = contacts;
        this.pending = pending;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.findlistitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Glide.with(mContext).asBitmap().load(profilePics.get(i)).into(viewHolder.profilePic);
        viewHolder.contactName.setText(contactNames.get(i));
        viewHolder.ContactParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext,ids.get(i), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.id = ids.get(i);
        viewHolder.email = emails.get(i);

        final Button butt = viewHolder.itemView.findViewById(R.id.addUser);

        //Add/remove contact handling code
        if(pending.contains(viewHolder.id)){
            Log.d("huhh",  pending.toString() + " does contain" + viewHolder.id);
            changeToPendingButton(butt);
        }
        if(contacts.contains(viewHolder.id)){
            changeToRemoveButton(butt);
        }
        //If not in pending or contacts
        if(!(contacts.contains(viewHolder.id) || pending.contains(viewHolder.id)) ){
            changeToAddButton(butt);
        }
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("FINDRECYCLER", "BUTTON CLICKED");

                String check = butt.getText().toString();

                switch (check.toUpperCase()){
                    case "ADD":
                        Log.d("FINDRECYCLER", "ADD CONTACT: " + viewHolder.id);
                        sendFriendRequest(viewHolder.email);
                        changeToPendingButton(butt);
                        addToPending(viewHolder.id);
                        break;

                    case "REMOVE":
                        Log.d("FINDRECYCLER", "REMOVE CONTACT: " + viewHolder.id);
                        removeContact(viewHolder);
                        changeToAddButton(butt);
                        break;
                }


            }
        });

    }

    public void changeToRemoveButton(Button butt){
        butt.setText("REMOVE");
        butt.setTextColor(Color.parseColor("#FF0040"));
        butt.setBackgroundResource(R.drawable.rm_button);

    }

    public void changeToPendingButton(Button butt){
        butt.setText("PENDING");
        disableButton(butt);
        butt.setTextColor(Color.parseColor("#505050"));
        butt.setBackgroundResource(R.drawable.rm_button);
    }

    public void changeToAddButton(Button butt){
        butt.setText("ADD");
        butt.setTextColor(Color.parseColor("#2A89F2"));
        butt.setBackgroundResource(R.drawable.rm_button);

    }

    public void disableButton(Button butt){
        butt.setEnabled(false);
    }

    public void addToPending(final String userToAdd){

        final String Uid = mAuth.getUid();

        db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.get("pendingRequests") != null){
                        db.collection("users").document(Uid).update("pendingRequests", FieldValue.arrayUnion(userToAdd));
                        Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED TO PENDING: " + userToAdd);
                    }
                    else{
                    }
                }
            }
        });
    }

    public void addContact(@NonNull final ViewHolder viewHolder){
        final String Uid = mAuth.getUid();

        db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                db.collection("users").document(Uid).update("contacts", FieldValue.arrayUnion(viewHolder.id));
                Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED: " + viewHolder.id);
            }
        });
    }

    public void removeContact(@NonNull final ViewHolder viewHolder) {
        final String Uid = mAuth.getUid();

        db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                db.collection("users").document(Uid).update("contacts", FieldValue.arrayRemove(viewHolder.id));
                Log.d("FINDRECYCLER", "SUCCESSFULLY REMOVED: " + viewHolder.id);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactNames.size();
    }

    //Basically the class of the entry itself
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView contactName;
        RelativeLayout ContactParentLayout;
        String email;
        String id;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            contactName = itemView.findViewById(R.id.contactName);
            ContactParentLayout = itemView.findViewById(R.id.contactParentLayout);

        }
    }

    public void filterList(ArrayList<String> contactNames, ArrayList<String> profilePics, ArrayList<String> ids, ArrayList<String> emails){
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.ids = ids;
        this.emails = emails;
        notifyDataSetChanged();
    }

    public void sendFriendRequest(String targetEmailInput){
        Log.d("DEBUG_SCANBARCODEACTIVITY", "sendFriendRequest: " + targetEmailInput);

        final String Uid = mAuth.getUid();
        Log.d(TAG, "findUid: " + targetEmailInput);
        final String targetEmail = targetEmailInput;

        final String[] targetUid = new String[1];
        targetUid[0]= null;

        //Start of search portion of method.
        Log.d(TAG, "findUid: This Uid "+ Uid);
        Task<QuerySnapshot> d = db.collection("users").get();
        d.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //Once list of users is retrieved,
                List<DocumentSnapshot> list = task.getResult().getDocuments(); //put into a list of users

                for (DocumentSnapshot doc : list) { //for each document in list,
                    if (!doc.getId().equals(Uid)) { //only check if not checking this user.
                        // String match.
                        String email = doc.get("email").toString();

                        if (compareContacts(targetEmail, email)) {
                            targetUid[0] = doc.getId();
                            Log.d(TAG, "onComplete: "+ targetUid[0]);
                            // If found, call the add method.
                            addSelfToUid(targetUid[0]);
                        }

                    }
                }
                Log.d(TAG, "onComplete1: "+ targetUid[0]);

            }
        });
    }

    public void addSelfToUid(String targetUidInput){
        final String targetUid = targetUidInput;
        final String selfUid = mAuth.getUid();
        if(targetUid!=null){
            db.collection("users").document(targetUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("DEBUG_SCANBARCODEACTIVITY", "addSelfToUid = " + targetUid);
                    db.collection("users").document(targetUid).update("requests", FieldValue.arrayUnion(selfUid));
//                            update("requests", selfUid);
                    Log.d(TAG, "addSelfToUid: SUCCESSFULLY ADDED " + selfUid + " to " + targetUid );
                }
            });
        } else {
            Log.d(TAG, "addSelfToUid: FAILED");

        }


    }

    public boolean addContactFromUid(String targetUidInput) {
        final String targetUid = targetUidInput;
        Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: " + targetUid);

        final String Uid = mAuth.getUid();
        Log.d(TAG, "addContact: line162 " + targetUid);

        if (targetUid != null) {
            Log.d("DEBUG_SCANBARCODEACTIVITY", "targetUid = " + targetUid);

            //
            db.collection("users").document(Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(Uid).update("contacts", FieldValue.arrayUnion(targetUid));
                    Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED: " + targetUid);
                }
            });



        } else {
            Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: COULD NOT FIND CONTACT");
            return false;
        }
        return false;
    }

    private boolean compareContacts(String text, String against){

        if(against.toUpperCase().contains(text.toUpperCase())){

            Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " SUCCESS");

            return true;
        }


        Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " FAILED");

        return false;
    }

    public void refreshData(){
        notifyDataSetChanged();
    }

}
