package com.moonstone.ezmaps_app.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.moonstone.ezmaps_app.ezchat.ChatActivity;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class requestsRecyclerViewAdapter extends RecyclerView.Adapter<requestsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;
    private ImageButton acceptButton;
    private ImageButton declineButton;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Bundle shareImageBundle;

    public requestsRecyclerViewAdapter(Context context, ArrayList<String> contactNames,
                                            ArrayList<String> profilePics, ArrayList<String> ids,
                                       FirebaseFirestore db, FirebaseAuth mAuth){

        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.db = db;
        this. mAuth = mAuth;

    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public requestsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.requestlistitem, viewGroup, false);

        requestsRecyclerViewAdapter.ViewHolder holder = new requestsRecyclerViewAdapter.ViewHolder(view);

        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull requestsRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        Picasso.get().load(profilePics.get(i)).into(viewHolder.profilePic);

        /*Glide.with(mContext)
                .asBitmap()
                .load(profilePics.get(i))
                .into(viewHolder.profilePic);*/

        viewHolder.contactName.setText(contactNames.get(i));


        //Add onclicklistener to each list entry
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUser = mAuth.getUid();
                final String target = ids.get(i);
                Log.d("ChooseContactRecyclerView", "onClick: " + ids.get(i));
                //add contact
                addContact(target, currentUser);
                addContact(currentUser, target);
                //delete requests on current user because added.
                deleteSelf(currentUser, target);
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUser = mAuth.getUid();
                final String target = ids.get(i);
                deleteSelf(currentUser, target);
            }
        });


        viewHolder.id = ids.get(i);
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
            acceptButton = itemView.findViewById(R.id.acceptReq);
            declineButton = itemView.findViewById(R.id.declineCall);

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



    public void addContact(final String currentUser, final String contactToBeAdded){
        final String UIDFrom = currentUser;

        if(contactToBeAdded != null){

            db.collection("users").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(currentUser).update("contacts", FieldValue.arrayUnion(contactToBeAdded));
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