package com.moonstone.ezmaps_app;


import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import butterknife.ButterKnife;
import butterknife.BindView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> contactNames = new ArrayList<>();
    private ArrayList<String> profilePics = new ArrayList<>();
    private Context mContext;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    public ContactRecyclerViewAdapter(Context context, ArrayList<String> contactNames, ArrayList<String> profilePics, ArrayList<String> ids, ArrayList<String> emails){
        Log.d("HERE", "INITIALISED ");
        this.contactNames = contactNames;
        this.profilePics = profilePics;
        this.mContext = context;
        this.ids = ids;
        this.emails = emails;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        //Gets the image and puts it into the referenced imageView
        Glide.with(mContext).asBitmap().load(profilePics.get(i)).into(viewHolder.profilePic);

        viewHolder.contactName.setText(contactNames.get(i));

        //Add onclicklistener to each list entry
        viewHolder.ContactParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext,contactNames.get(i), Toast.LENGTH_SHORT).show();

                //--------------------------------READ THIS----------------------//
                //--Here's where you would link to the messaging page for that person
                //-----ids.get(i) will give you the id of the person you clicked on which can then be linked to message functionality
                // E.g. sendMessageTo(ids.get(i));
                Log.d("ContactRecyclerView", "This Device token: "+ MyFirebaseMessagingService.fetchToken());
                Log.d("ContactRecyclerView", "onClick: " + ids.get(i));


                //-------------------------PLS HELP------------------------------
                // Need this to redirect to chat_page
//                Intent intent = new Intent(, FrontPage.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
                ChatActivity.setToUserID(ids.get(i));
                ChatActivity.setFromUserID(MyFirebaseMessagingService.fetchToken());


                mContext.startActivity(new Intent(mContext, ChatActivity.class));


            }
        });

        viewHolder.id = ids.get(i);
        viewHolder.email = emails.get(i);

        //last one
        if(i == contactNames.size() - 1){
            Tab3Fragment.contactsLoading.setVisibility(View.GONE);
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
