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

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<EzMessage> textMessages = new ArrayList<>();
    private Context mContext;

    //Never rendered but information is held here
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();

    public MessageRecyclerViewAdapter(Context context, ArrayList<EzMessage> textMessages){
        Log.d("HERE", "INITIALISED ");
        this.textMessages = textMessages;
        this.mContext = context;
    }

    //Actually recycles the view holders
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelistitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    //Called every time a new item is added to the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d("HERE", Integer.toString(i));

        //Gets the image and puts it into the referenced imageView

        viewHolder.messageText.setText(textMessages.get(i).getText());

        //Add onclicklistener to each list entry
        viewHolder.MessageParentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext,textMessages.get(i), Toast.LENGTH_SHORT).show();

                //--------------------------------READ THIS----------------------//
                //--Here's where you would link to the messaging page for that person
                //-----ids.get(i) will give you the id of the person you clicked on which can then be linked to message functionality
                // E.g. sendMessageTo(ids.get(i));
                Log.d("MessageRecyclerView", "This Device token: "+ MyFirebaseMessagingService.fetchToken());
                Log.d("MessageRecyclerView", "onClick: " + ids.get(i));


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

        //last one
        if(i == textMessages.size() - 1){
//            Tab3Fragment.contactsLoading.setVisibility(View.GONE);
        }
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

    public void filterList(ArrayList<String> textMessages){
        this.textMessages = textMessages;
        notifyDataSetChanged();
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
