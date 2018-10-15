package com.moonstone.ezmaps_app.contact;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<FriendRequestsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    private Bundle shareImageBundle;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String TAG = "DEBUGFriendRequestsRecyclerViewAdapter";



    public FriendRequestsRecyclerViewAdapter(Context context, ArrayList<String> contactNames,
                                             ArrayList<String> profilePics, ArrayList<String> ids,
                                             ArrayList<String> emails, FirebaseFirestore db, FirebaseAuth mAuth){

        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.emails = emails;
        this.mActivity = mActivity;
        this.shareImageBundle = shareImageBundle;
        this.db = db;
        this.mAuth = mAuth;

    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public FriendRequestsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contactlistitem, viewGroup, false);

        FriendRequestsRecyclerViewAdapter.ViewHolder holder = new FriendRequestsRecyclerViewAdapter.ViewHolder(view);

        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull FriendRequestsRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        //Gets the image and puts it into the referenced imageView

        if(mContext != null){
            Log.d("ChooseContactRecycler", "Context: " + mContext);
        }

        if(profilePics.get(i) != null){
            Log.d("ChooseContactRecycler", "profile pic: " + profilePics.get(i));
        }


        Log.d("ChooseContactRecycler", "position: " + i );

        Picasso.get().load(profilePics.get(i)).into(viewHolder.profilePic);

        /*Glide.with(mContext)
                .asBitmap()
                .load(profilePics.get(i))
                .into(viewHolder.profilePic);*/

        viewHolder.contactName.setText(contactNames.get(i));


        //Add onclicklistener to each list entry
        viewHolder.ContactParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                final String currentUser = mAuth.getUid();
                final String target = ids.get(i);
                Log.d("ChooseContactRecyclerView", "onClick: " + ids.get(i));
                //add contact
                addContact(target, currentUser);
                addContact(currentUser, target);
                //delete requests on current user because added.
                deleteSelf(currentUser, target);

                refreshData();


            }
        });

        viewHolder.id = ids.get(i);
        viewHolder.email = emails.get(i);

        //last one
        /*if(i == contactNames.size() - 1){
            Tab3Fragment.contactsLoading.setVisibility(View.GONE);
        }*/
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

            profilePic = itemView.findViewById(R.id.callerPic);
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
                emails.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


    public void addContact(final String currentUser, final String contactToBeAdded){
        final String UIDFrom = currentUser;

        //Start of search portion of method.
        Log.d(TAG, "findUid: This Uid "+ UIDFrom);

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
            Log.d(TAG, "addContact: COULD NOT FIND CONTACT");
        }
    }

    public void deleteSelf(final String currentUser, final String contactToBeDeleted){

        if(contactToBeDeleted != null){

            db.collection("users").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    db.collection("users").document(currentUser).update("requests", FieldValue.arrayRemove(contactToBeDeleted));
                    Log.d(TAG, "SUCCESSFULLY DELETED: " + contactToBeDeleted);
                }
            });

        }
    }



//    public boolean addContactFromUid(String targetUidInput) {
//        final String targetUid = targetUidInput;
//        Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: " + targetUid);
//
//        final String Uid = mAuth.getUid();
//        Log.d(TAG, "addContact: line162 " + targetUid);
//
//        if (targetUid != null) {
//            Log.d("DEBUG_SCANBARCODEACTIVITY", "targetUid = " + targetUid);
//
//            //
//
//
//
//
//        } else {
//            Log.d("DEBUG_SCANBARCODEACTIVITY", "addContact: COULD NOT FIND CONTACT");
//            return false;
//        }
//        return false;
//    }



//
//    private boolean compareContacts(String text, String against){
//
//        if(against.toUpperCase().contains(text.toUpperCase())){
//
//            Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " SUCCESS");
//
//            return true;
//        }
//
//
//        Log.d("Add Contacts", "Comparing string1: " + text + " in string2: " + against + " FAILED");
//
//        return false;
//    }

}