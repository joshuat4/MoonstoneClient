package com.moonstone.ezmaps_app;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindRecyclerViewAdapter extends RecyclerView.Adapter<FindRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private ArrayList<String> contacts = new ArrayList<>();
    private Context mContext;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FindRecyclerViewAdapter(Context context, ArrayList<String> contactNames, ArrayList<String> profilePics,
                                   ArrayList<String> ids, ArrayList<String> emails, ArrayList<String> contacts){
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.emails = emails;
        this.contacts = contacts;

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

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        //Gets the image and puts it into the referenced imageView
        Glide.with(mContext).asBitmap().load(profilePics.get(i)).into(viewHolder.profilePic);

        viewHolder.contactName.setText(contactNames.get(i));

        //Add onclicklistener to each list entry
        viewHolder.ContactParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext,contactNames.get(i), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.id = ids.get(i);
        viewHolder.email = emails.get(i);

        final Button butt = viewHolder.itemView.findViewById(R.id.addUser);

        //Add/remove contact handling code
        if(contacts.contains(viewHolder.id)){
            butt.setText("REMOVE");
            butt.setTextColor(Color.parseColor("#FF0040"));
            butt.setBackgroundResource(R.drawable.rm_button);
        }

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("FINDRECYCLER", "BUTTON CLICKED");

                String check = butt.getText().toString();

                switch (check.toUpperCase()){
                    case "ADD":
                        Log.d("FINDRECYCLER", "ADD CONTACT: " + viewHolder.id);
                        addContact(viewHolder);
                        butt.setText("REMOVE");
                        break;

                    case "REMOVE":
                        Log.d("FINDRECYCLER", "REMOVE CONTACT: " + viewHolder.id);
                        removeContact(viewHolder);
                        butt.setText("ADD");
                        break;

                }

            }
        });

        /*if(i == contactNames.size() - 1){
            com.moonstone.ezmaps_app.NewContactSearch.findContactsLoading.setVisibility(View.GONE);
        }*/
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

    public void refreshData(){
        notifyDataSetChanged();
    }

}
