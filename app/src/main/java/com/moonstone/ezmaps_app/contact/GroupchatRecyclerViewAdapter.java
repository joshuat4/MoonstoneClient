package com.moonstone.ezmaps_app.contact;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezchat.GroupchatActivity;
import com.moonstone.ezmaps_app.ezchat.MyFirebaseMessagingService;

import java.util.ArrayList;

public class GroupchatRecyclerViewAdapter extends RecyclerView.Adapter<GroupchatRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> groupchatNames = new ArrayList<>();
    private ArrayList<Boolean> unread = new ArrayList<>();
    private ArrayList<String> groupchatOrder = new ArrayList<>();
    private ArrayList<String> unreadGroupchatOrder = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;

    private ArrayList<Integer> mSelected = new ArrayList<>();

    private ArrayList<String> groupchats = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public GroupchatRecyclerViewAdapter(Context context, ArrayList<String> groupchatNames,
                                        ArrayList<String> groupchats, ArrayList<Boolean> unread,
                                        ArrayList<String> groupchatOrder,
                                        ArrayList<String> unreadGroupchatOrder,
                                        FirebaseFirestore db, FirebaseAuth mAuth){
        Log.d("groupchat", "INITIALISED ");
        this.groupchatNames = groupchatNames;
        this.mContext = context;
        this.groupchats = groupchats;
        this.unread = unread;
        this.groupchatOrder = groupchatOrder;
        this.unreadGroupchatOrder = unreadGroupchatOrder;
        this.db = db;
        this.mAuth = mAuth;
    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groupchatlistitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        viewHolder.GroupchatParentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        viewHolder.groupchatName.setText(groupchatNames.get(i));
        Log.d("groupchat", "groupchat Names: " + groupchatNames.toString());

        //set unread message notification visibility
        if(unread.size() > 0){
            if(unreadGroupchatOrder.contains(groupchatOrder.get(i))){
                if(unread.get(unreadGroupchatOrder.indexOf(groupchatOrder.get(i)))){
                    Log.d("groupchat", "unreadGroupchatOrder: " + unreadGroupchatOrder.toString());
                    Log.d("groupchat", "groupchatOrder: " + groupchatOrder.toString());
                    Log.d("groupchat", "unread: " + unread.toString());
                    viewHolder.unreadNotification.setVisibility(View.VISIBLE);
                } else if(!unread.get(unreadGroupchatOrder.indexOf(groupchatOrder.get(i)))) {
                    Log.d("unread", "invisible");
                    viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
                }
            } else {
                viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.d("unread", "invisible, no unread list");
            viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
        }

        //Add onclicklistener to each list entry
        viewHolder.GroupchatParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Log.d("GroupchatRecyclerView", "This Device token: "+ MyFirebaseMessagingService.fetchToken());

                Log.d("GroupchatRecyclerView", "groupchats: "+ groupchats.toString());

                Log.d("groupchat", "INSIDE RECVIEW groupchatNames: " + groupchatNames.toString() + "\ngroupchats: "
                        + groupchats.toString() + "\ngroupchatUnread: " + unread.toString() +
                        "\ngroupchatOrder: " + groupchatOrder.toString() + "\ngroupchatUnreadOrder: " +
                        unreadGroupchatOrder.toString());

                db.collection("groupchats").document(groupchatOrder.get(i)).get()
                        .addOnSuccessListener(
                                new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Log.d("groupchat", "current group: " + groupchatOrder.get(i));

                                        final ArrayList<String> groupchatMemberIds = (ArrayList<String>) documentSnapshot.get("ids");
                                        GroupchatActivity.setToUserIds(groupchatMemberIds);
                                        GroupchatActivity.setGroupchatId(groupchatOrder.get(i));

                                        GroupchatActivity.setFromUserID(mAuth.getUid());
                                        String name = groupchatNames.get(i);
                                        Intent i = new Intent(mContext, GroupchatActivity.class);
                                        i.putExtra("name", name);
                                        i.putExtra("fromChooseGroupchats", false);
                                        mContext.startActivity(i);
                                    }
                                });


            }
        });

        if(groupchatOrder.size() > 0) {
            viewHolder.id = groupchatOrder.get(i);
        }
    }


    @Override
    public int getItemCount() {
        return groupchatNames.size();
    }

    //Basically the class of the entry itself
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupchatName;
        TextView unreadNotification;
        RelativeLayout GroupchatParentLayout;
        String id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupchatName = itemView.findViewById(R.id.groupchatName);
            GroupchatParentLayout = itemView.findViewById(R.id.groupchatParentLayout);
            unreadNotification = itemView.findViewById(R.id.unreadNotification);

        }
    }

    public void filterList(ArrayList<String> groupchatNames, ArrayList<String> groupchats,
                           ArrayList<Boolean> unread, ArrayList<String> groupchatOrder,
                           ArrayList<String> unreadGroupchatOrder){
        Log.d("HERE", "FILTERED ");
        this.groupchatNames = groupchatNames;
        this.groupchats = groupchats;
        this.unread = unread;
        this.groupchatOrder = groupchatOrder;
        this.unreadGroupchatOrder = unreadGroupchatOrder;
        notifyDataSetChanged();
    }

    public void refreshData(){
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = groupchatNames.size();
        groupchatNames.clear();
        groupchats.clear();
        unread.clear();
        groupchatOrder.clear();
        unreadGroupchatOrder.clear();
        notifyItemRangeRemoved(0, size);
    }


}