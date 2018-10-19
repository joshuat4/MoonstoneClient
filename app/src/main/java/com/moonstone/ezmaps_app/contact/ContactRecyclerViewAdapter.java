package com.moonstone.ezmaps_app.contact;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezchat.ChatActivity;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;
import com.moonstone.ezmaps_app.main.Tab3Fragment;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;
    private Tab3Fragment fragment;
    private FirebaseAuth mAuth;
    private ArrayList<Integer> mSelected = new ArrayList<>();

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    public ContactRecyclerViewAdapter(Tab3Fragment fragment, Context context, Activity mActivity,
                                      ArrayList<String> contactNames,
                                      ArrayList<String> profilePics, ArrayList<String> ids,
                                      ArrayList<String> emails){
        Log.d("HERE", "INITIALISED ");
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.fragment = fragment;
        this.ids = ids;
        this.emails = emails;
        this.mActivity = mActivity;
        mAuth = FirebaseAuth.getInstance();
    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contactlistitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        if(ids.size() != 0) {
            //Gets the image and puts it into the referenced imageView
            Glide.with(mContext).asBitmap().load(profilePics.get(i)).into(viewHolder.profilePic);

            viewHolder.contactName.setText(contactNames.get(i));

            //Add onclicklistener to each list entry
            viewHolder.ContactParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ids.size() != 0) {
                        Log.d("ContactRecyclerView", "This Device token: " + MyFirebaseMessagingService.fetchToken());
                        Log.d("ContactRecyclerView", "onClick: " + ids.get(i));

                        if (Tab3Fragment.checked == false) {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();

                            ChatActivity.setToUserID(ids.get(i));
                            ChatActivity.setFromUserID(mAuth.getUid());
                            String name = contactNames.get(i);
                            String profilePic = profilePics.get(i);
                            Intent i = new Intent(mContext, ChatActivity.class);
                            i.putExtra("name", name);
                            i.putExtra("picture", profilePic);
                            i.putExtra("fromChooseContacts", false);
                            mContext.startActivity(i);
                            mActivity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else if (!mSelected.contains(i)) {
                            mSelected.add(i);
                            viewHolder.ContactParentLayout.setBackgroundColor(Color.parseColor("#00FFFF"));
                        } else if (mSelected.contains(i)) {
                            mSelected.remove((Object) i);
                            viewHolder.ContactParentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        }
                    }
                }
            });

            viewHolder.id = ids.get(i);
            viewHolder.email = emails.get(i);

            //last one
            if (i == contactNames.size() - 1) {
                fragment.updateLoaded(RecView.CONTACTS);
            }
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

    public void clearSelected() {
        mSelected.clear();
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedIds() {
        ArrayList<String> selectedUserIds = new ArrayList<>();
        for(int i = 0; i<mSelected.size(); i++){
            selectedUserIds.add(ids.get(mSelected.get(i)));
        }
        return selectedUserIds;
    }

    public ArrayList<String> getSelectedNames() {
        ArrayList<String> selectedUserNames = new ArrayList<>();
        for(int i = 0; i<mSelected.size(); i++){
            selectedUserNames.add(contactNames.get(mSelected.get(i)));
        }
        return selectedUserNames;
    }
}