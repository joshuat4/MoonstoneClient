package com.moonstone.ezmaps_app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.moonstone.ezmaps_app.EzMessage;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<EzMessage> textMessages = new ArrayList<>();
    private Context mContext;
    private FirebaseAuth mAuth;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    private int testing = 0;

    public MessageRecyclerViewAdapter(Context context, ArrayList<EzMessage> textMessages){
        Log.d("messages", textMessages.toString());
        this.textMessages = textMessages;
        this.mContext = context;
    }


        //Actually recycles the view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mAuth = FirebaseAuth.getInstance();
        final String Uid = mAuth.getUid();
        if(textMessages.get(i).getFromUserId().equals(Uid)){
            Log.d("messages", "view holder 1 " + Integer.toString(i));
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelistitemself, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            holder.setIsRecyclable(false);
            return holder;
        }
        else{
            Log.d("messages", "view holder 2 " + Integer.toString(i));
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelistitem, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            holder.setIsRecyclable(false);
            return holder;
        }
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d("messages", "view" + Integer.toString(i));

        //Gets the image and puts it into the referenced imageView

        viewHolder.messageText.setText(textMessages.get(i).getText());

        //Add onclicklistener to each list entry
        viewHolder.MessageParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext,textMessages.get(i).getText(), Toast.LENGTH_SHORT).show();

            }
        });

        //last one
//        if(i == textMessages.size() - 1){
//            chat_page.messagesLoading.setVisibility(View.GONE);
//        }
    }


    @Override
    public int getItemCount() {
        return textMessages.size();
    }

    //Basically the class of the entry itself
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        RelativeLayout MessageParentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }


    public void refreshData(){
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = textMessages.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                textMessages.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


}
