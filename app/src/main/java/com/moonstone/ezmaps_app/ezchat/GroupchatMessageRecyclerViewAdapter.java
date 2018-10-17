package com.moonstone.ezmaps_app.ezchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupchatMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<EzMessage> textMessages = new ArrayList<>();
    private Context mContext;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String fromUsersName;

    private int testing = 0;

    public GroupchatMessageRecyclerViewAdapter(Context context, ArrayList<EzMessage> textMessages,
                                               FirebaseFirestore db){
        Log.d("messages", textMessages.toString());
        this.textMessages = textMessages;
        this.mContext = context;
        this.db = db;
        mAuth = FirebaseAuth.getInstance();

    }

    private static final int LAYOUT_IMAGE_SELF = 0;
    private static final int LAYOUT_IMAGE_OTHERS = 1;
    private static final int LAYOUT_TEXT_SELF = 2;
    private static final int LAYOUT_TEXT_OTHERS = 3;

    @Override
    public int getItemViewType(int position) {
        final String Uid = mAuth.getUid();
        boolean isSelf;

        if(textMessages.get(position).getFromUserId().equals(Uid)){
            isSelf = true;

        }else {
            isSelf = false;
        }

        switch (textMessages.get(position).getTextType()){
            case "IMAGE":
                if(isSelf){
                    return LAYOUT_IMAGE_SELF;
                }else{
                    return LAYOUT_IMAGE_OTHERS;
                }

            case "TEXT":
                if(isSelf){
                    return LAYOUT_TEXT_SELF;
                }else{
                    return LAYOUT_TEXT_OTHERS;
                }

            default:
                if(isSelf){
                    return LAYOUT_TEXT_SELF;
                }else{
                    return LAYOUT_TEXT_OTHERS;
                }
        }

    }




    //Actually recycles the view holders
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        mAuth = FirebaseAuth.getInstance();
        final String Uid = mAuth.getUid();

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (position) {
            case LAYOUT_IMAGE_SELF:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_image_self, viewGroup, false);
                viewHolder = new ViewHolderImageSelf(view);
                break;

            case LAYOUT_IMAGE_OTHERS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groupchat_list_item_image_others, viewGroup, false);
                viewHolder = new ViewHolderImageOthers(view);
                break;

            case LAYOUT_TEXT_SELF:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_self, viewGroup, false);
                viewHolder = new ViewHolderTextSelf(view);
                break;

            case LAYOUT_TEXT_OTHERS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groupchat_list_item_others, viewGroup, false);
                viewHolder = new ViewHolderTextOthers(view);
                break;

        }


        viewHolder.setIsRecyclable(false);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if(viewHolder instanceof ViewHolderTextSelf){
            ViewHolderTextSelf holder = (ViewHolderTextSelf) viewHolder;
            final int holderPos = holder.getAdapterPosition();

            holder.messageText.setText(textMessages.get(holderPos).getText());
            Log.d("MessageRecyclerView", "Text Set: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();
                }
            });


        } else if(viewHolder instanceof ViewHolderImageSelf) {
            ViewHolderImageSelf holder = (ViewHolderImageSelf) viewHolder;
            final int holderPos = holder.getAdapterPosition();

            Picasso.get().load(textMessages.get(holderPos).getText()).into(holder.messageText);
            Log.d("GroupchatMessageRecyclerView", "Image Load into: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when clicked zoom in to image
                    Toast.makeText(mContext, textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();

                }
            });

        } else if(viewHolder instanceof ViewHolderTextOthers){
            final ViewHolderTextOthers holder = (ViewHolderTextOthers) viewHolder;
            final int holderPos = holder.getAdapterPosition();
            db.collection("users").document(textMessages
                    .get(holderPos).getFromUserId()).get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            fromUsersName = documentSnapshot.getString("name");
                            holder.fromUserName.setText(fromUsersName);
                        }
                    });

            holder.messageText.setText(textMessages.get(holderPos).getText());
            Log.d("MessageRecyclerView", "Text Set: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();
                }
            });


        }else if (viewHolder instanceof ViewHolderImageOthers){
            final ViewHolderImageOthers holder = (ViewHolderImageOthers) viewHolder;
            final int holderPos = holder.getAdapterPosition();
            db.collection("users").document(textMessages
                    .get(holderPos).getFromUserId()).get().addOnSuccessListener(
                            new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    fromUsersName = documentSnapshot.getString("name");
                                    holder.fromUserName.setText(fromUsersName);
                                }
                            });

            Picasso.get().load(textMessages.get(holderPos).getText()).into(holder.messageText);

            Log.d("GroupchatMessageRecyclerView", "Image Load into: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when clicked zoom in to image
                    Toast.makeText(mContext,textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();

                }
            });


        }


    }

    //differing classes based on whether the message came from you or not (so as to know to
    // display 'from username' or not, and whether the message is a text or image type
    public class ViewHolderTextSelf extends RecyclerView.ViewHolder {
        TextView messageText;
        RelativeLayout MessageParentLayout;

        public ViewHolderTextSelf(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }

    public class ViewHolderTextOthers extends RecyclerView.ViewHolder {
        TextView messageText;
        RelativeLayout MessageParentLayout;
        TextView fromUserName;

        public ViewHolderTextOthers(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            fromUserName = itemView.findViewById(R.id.FromUserName);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }

    public class ViewHolderImageOthers extends RecyclerView.ViewHolder {
        ImageView messageText;
        RelativeLayout MessageParentLayout;
        TextView fromUserName;

        public ViewHolderImageOthers(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            fromUserName = itemView.findViewById(R.id.FromUserName);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }

    public class ViewHolderImageSelf extends RecyclerView.ViewHolder {
        ImageView messageText;
        RelativeLayout MessageParentLayout;

        public ViewHolderImageSelf(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }

    @Override
    public int getItemCount() {
        return textMessages.size();
    }


    public void refreshData(){
        notifyDataSetChanged();
    }

    public void clear() {
        final int size = textMessages.size();
        if (size > 0) {
            textMessages.clear();

            notifyItemRangeRemoved(0, size);
        }
    }


}
