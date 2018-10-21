package com.moonstone.ezmaps_app.contact;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.main.Tab3Fragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsRecyclerViewAdapter extends RecyclerView.Adapter<RequestsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;
    private ImageButton acceptButton;
    private ImageButton declineButton;
    private Tab3Fragment fragment;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Bundle shareImageBundle;

    public RequestsRecyclerViewAdapter(Tab3Fragment fragment, Context context, ArrayList<String> contactNames,
                                       ArrayList<String> profilePics, ArrayList<String> ids,
                                       FirebaseFirestore db, FirebaseAuth mAuth){

        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.fragment = fragment;
        this.ids = ids;
        this.db = db;
        this. mAuth = mAuth;

    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public RequestsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.requestlistitem, viewGroup, false);

        RequestsRecyclerViewAdapter.ViewHolder holder = new RequestsRecyclerViewAdapter.ViewHolder(view);

        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull RequestsRecyclerViewAdapter.ViewHolder viewHolder, final int i) {

        Picasso.get().load(profilePics.get(i)).into(viewHolder.profilePic);

        viewHolder.contactName.setText(contactNames.get(i));

        acceptButton = viewHolder.itemView.findViewById(R.id.acceptReq);
        declineButton = viewHolder.itemView.findViewById(R.id.declineReq);

        //Describes how the accept and decline buttons work within the request view

        //Add onclicklistener to each list entry
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUser = mAuth.getUid();
                if(ids.size() != 0) {
                    final String target = ids.get(i);
                    //add contact
                    addContact(target, currentUser);
                    addContact(currentUser, target, target);
                    contactNames.remove(i);
                    profilePics.remove(i);
                    ids.remove(i);
                    fragment.addToRespondedRequests(target);
                    refreshData();
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUser = mAuth.getUid();
                if(ids.size() != 0) {
                    final String target = ids.get(i);
                    deleteSelf(currentUser, target);
                    contactNames.remove(i);
                    profilePics.remove(i);
                    ids.remove(i);
                    refreshData();
                }
            }
        });


        viewHolder.id = ids.get(i);

        //last one
        if(i == contactNames.size() - 1){
            fragment.updateLoaded(RecView.REQUESTS);
        }
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
        String id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.reqCallerPic);
            contactName = itemView.findViewById(R.id.reqContactName);
            ContactParentLayout = itemView.findViewById(R.id.contactParentLayout);

        }
    }

    public void filterList(ArrayList<String> contactNames, ArrayList<String> profilePics, ArrayList<String> ids){
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.ids = ids;
        notifyDataSetChanged();
    }

    public void refreshData(){
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = contactNames.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                contactNames.remove(0);
                profilePics.remove(0);
                ids.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


    public void addContact(final String currentUser, final String contactToBeAdded, final String contactToBeDeleted){
        final String UIDFrom = currentUser;

        if(contactToBeAdded != null){

            db.collection("users").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(currentUser).update("contacts", FieldValue.arrayUnion(contactToBeAdded));
                    db.collection("users").document(currentUser).update("contacts", FieldValue.arrayUnion(contactToBeAdded), "requests", FieldValue.arrayRemove(contactToBeDeleted));
                    Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED: " + contactToBeAdded);
                }
            });

        }

        else{
        }
    }

    public void addContact(final String currentUser, final String contactToBeAdded){
        //target, currentUser
        final String UIDFrom = currentUser;

        if(contactToBeAdded != null){

            db.collection("users").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(currentUser).update("contacts", FieldValue.arrayUnion(contactToBeAdded), "pendingRequests", FieldValue.arrayRemove(contactToBeAdded));
                    Log.d("FINDRECYCLER", "SUCCESSFULLY ADDED: " + contactToBeAdded);
                }
            });

        }

        else{
        }
    }

    public void deleteSelf(final String currentUser, final String contactToBeDeleted){

        if(contactToBeDeleted != null){

            db.collection("users").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(currentUser).update("requests", FieldValue.arrayRemove(contactToBeDeleted));
                }
            });

        }
    }

}