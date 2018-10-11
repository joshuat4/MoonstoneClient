package com.moonstone.ezmaps_app.contact;

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
import com.google.firebase.auth.FirebaseAuth;
import com.moonstone.ezmaps_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                viewHolder = new ViewHolderImage(view);
                break;

            case LAYOUT_IMAGE_OTHERS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_image_others, viewGroup, false);
                viewHolder = new ViewHolderImage(view);
                break;

            case LAYOUT_TEXT_SELF:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_self, viewGroup, false);
                viewHolder = new ViewHolderText(view);
                break;

            case LAYOUT_TEXT_OTHERS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_others, viewGroup, false);
                viewHolder = new ViewHolderText(view);
                break;

        }


        viewHolder.setIsRecyclable(false);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if(viewHolder instanceof ViewHolderText){
            ViewHolderText holder = (ViewHolderText) viewHolder;
            final int holderPos = holder.getAdapterPosition();

            holder.messageText.setText(textMessages.get(holderPos).getText());
            Log.d("MessageRecyclerView", "Text Set: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();
                }
            });


        }else if (viewHolder instanceof ViewHolderImage){
            ViewHolderImage holder = (ViewHolderImage) viewHolder;
            final int holderPos = holder.getAdapterPosition();

            Picasso.get().load(textMessages.get(holderPos).getText()).into(holder.messageText);
            Log.d("MessageRecyclerView", "Image Load into: " + textMessages.get(holderPos).getText());

            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when clicked zoom in to image
                    Toast.makeText(mContext,textMessages.get(holderPos).getText(), Toast.LENGTH_SHORT).show();

                }
            });


        }


    }

    //Basically the class of the entry itself
    public class ViewHolderText extends RecyclerView.ViewHolder {
        TextView messageText;
        RelativeLayout MessageParentLayout;

        public ViewHolderText(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            MessageParentLayout = itemView.findViewById(R.id.messageParentLayout);

        }
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {
        ImageView messageText;
        RelativeLayout MessageParentLayout;

        public ViewHolderImage(@NonNull View itemView) {
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
            for (int i = 0; i < size; i++) {
                textMessages.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


}
