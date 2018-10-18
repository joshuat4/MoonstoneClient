package com.moonstone.ezmaps_app.contact;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.moonstone.ezmaps_app.main.Tab3Fragment;

import java.util.ArrayList;

public class GroupchatRecyclerViewAdapter extends RecyclerView.Adapter<GroupchatRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> groupchatNames = new ArrayList<>();
    private ArrayList<ArrayList<String>> groupchatUserIds = new ArrayList<>();
    private ArrayList<Boolean> unread = new ArrayList<>();
    private ArrayList<String> groupchatOrder = new ArrayList<>();
    private ArrayList<String> unreadGroupchatOrder = new ArrayList<>();
    private Context mContext;
    private Activity mActivity;
    private Tab3Fragment fragment;

    private ArrayList<Integer> mSelected = new ArrayList<>();

    private ArrayList<String> groupchats = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public GroupchatRecyclerViewAdapter(Tab3Fragment fragment, Context context, ArrayList<String> groupchatIds, ArrayList<ArrayList<String>> groupchatNames, ArrayList<ArrayList<String>> groupchatUserIds, FirebaseFirestore db, FirebaseAuth mAuth){
        Log.d("groupchat", "INITIALISED ");
        this.groupchatNames = groupchatNames;
        this.mContext = context;
        this.fragment = fragment;
        this.groupchats = groupchatIds;
        this.groupchatUserIds = groupchatUserIds;
//        this.unread = unread;
//        this.groupchatOrder = groupchatOrder;
//        this.unreadGroupchatOrder = unreadGroupchatOrder;
        this.db = db;
        this.mAuth = mAuth;
        Log.d("GroupchatRecyclerView", "groupchats: "+ groupchats.toString());
        Log.d("GroupchatRecyclerView", "userids: "+ groupchatUserIds.toString());
        Log.d("GroupchatRecyclerView", "usernames: "+ groupchatNames.toString());
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

        //format the list of names into a printable manner
        String editString = new String();
        Log.d("groupchat", "onbndvwhldr groupchat names: " + groupchatNames.size());
        Log.d("groupchat", "onbndvwhldr groupchat names: " + groupchatNames.toString());

        for(int step = 0; step<groupchatNames.get(i).size(); step++){
            if(step != (groupchatNames.get(i).size() - 1)){
                Log.d("groupchat", "i rn: "+i);
                editString = editString.concat(groupchatNames.get(i).get(step)+", ");
                Log.d("groupchat", "name appended: "+ groupchatNames.get(i).get(step));
            } else {
                editString = editString.concat(groupchatNames.get(i).get(step));
            }
        }
        Log.d("groupchat", "edited string name: " + editString);

        viewHolder.groupchatName.setText(editString);

        //set unread message notification visibility
//        if(unread.size() > 0){
//            if(unreadGroupchatOrder.contains(groupchatOrder.get(i))){
//                if(unread.get(unreadGroupchatOrder.indexOf(groupchatOrder.get(i)))){
//                    Log.d("groupchat", "unreadGroupchatOrder: " + unreadGroupchatOrder.toString());
//                    Log.d("groupchat", "groupchatOrder: " + groupchatOrder.toString());
//                    Log.d("groupchat", "unread: " + unread.toString());
//                    viewHolder.unreadNotification.setVisibility(View.VISIBLE);
//                } else if(!unread.get(unreadGroupchatOrder.indexOf(groupchatOrder.get(i)))) {
//                    Log.d("unread", "invisible");
//                    viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
//            }
//        } else {
//            Log.d("unread", "invisible, no unread list");
//            viewHolder.unreadNotification.setVisibility(View.INVISIBLE);
//        }

        //Add onclicklistener to each list entry
        viewHolder.GroupchatParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Log.d("GroupchatRecyclerView", "This Device token: "+ MyFirebaseMessagingService.fetchToken());


                GroupchatActivity.setToUserIds(groupchatUserIds.get(i));
                GroupchatActivity.setGroupchatId(groupchats.get(i));

                GroupchatActivity.setFromUserID(mAuth.getUid());
                String name = (String) viewHolder.groupchatName.getText();
                Intent i = new Intent(mContext, GroupchatActivity.class);
                i.putExtra("name", name);
                i.putExtra("fromChooseGroupchats", false);
                mContext.startActivity(i);
            }
        });

        //last one
        if(i == groupchatNames.size() - 1){
            fragment.updateLoaded(RecView.GROUPCHATS);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupchatName = itemView.findViewById(R.id.groupchatName);
            GroupchatParentLayout = itemView.findViewById(R.id.groupchatParentLayout);
            unreadNotification = itemView.findViewById(R.id.unreadNotification);

        }
    }


    public void refreshData(){
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = groupchatNames.size();
        if(size > 0){
            for(int i =0; i<size; i++){
                groupchatNames.remove(0);
                groupchats.remove(0);
                unread.remove(0);
                groupchatOrder.remove(0);
                unreadGroupchatOrder.remove(0);
            }
        }
        notifyItemRangeRemoved(0, size);
    }


}