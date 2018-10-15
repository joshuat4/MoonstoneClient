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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezchat.ChatActivity;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseContactRecyclerViewAdapter extends RecyclerView.Adapter<ChooseContactRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    private Bundle shareImageBundle;

    public ChooseContactRecyclerViewAdapter(Context context, Activity mActivity, ArrayList<String> contactNames,
                                      ArrayList<String> profilePics, ArrayList<String> ids,
                                      ArrayList<String> emails, Bundle shareImageBundle){

        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.emails = emails;
        this.mActivity = mActivity;
        this.shareImageBundle = shareImageBundle;

    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public ChooseContactRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contactlistitem, viewGroup, false);

        ChooseContactRecyclerViewAdapter.ViewHolder holder = new ChooseContactRecyclerViewAdapter.ViewHolder(view);

        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull ChooseContactRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
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

                Log.d("ChooseContactRecyclerView", "This Device token: "+ MyFirebaseMessagingService.fetchToken());
                Log.d("ChooseContactRecyclerView", "onClick: " + ids.get(i));

                ChatActivity.setToUserID(ids.get(i));
                ChatActivity.setFromUserID(MyFirebaseMessagingService.fetchToken());
                String name = contactNames.get(i);
                Intent i = new Intent(mContext, ChatActivity.class);
                i.putExtras(shareImageBundle);
                i.putExtra("name", name);

                mActivity.startActivity(i);
                mActivity.overridePendingTransition(R.anim.enter, R.anim.exit);
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
}