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
    }

    private static final int LAYOUT_IMAGE= 0;
    private static final int LAYOUT_TEXT= 1;

    @Override
    public int getItemViewType(int position) {

        if(textMessages.get(position).getTextType() == null){
            return LAYOUT_TEXT;
        }


        if(textMessages.get(position).getTextType().equals("IMAGE")){
            return LAYOUT_IMAGE;

        }else{
            return LAYOUT_TEXT;
        }
    }




    //Actually recycles the view holders
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mAuth = FirebaseAuth.getInstance();
        final String Uid = mAuth.getUid();

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        if(textMessages.get(i).getFromUserId().equals(Uid)){
            if(i == LAYOUT_IMAGE){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_image_self, viewGroup, false);
                viewHolder = new ViewHolderImage(view);

            }else{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_self, viewGroup, false);
                viewHolder = new ViewHolderText(view);

            }


        }else{
            if(i == LAYOUT_IMAGE){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_image, viewGroup, false);
                viewHolder = new ViewHolderImage(view);

            }else{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item, viewGroup, false);
                viewHolder = new ViewHolderText(view);

            }

        }

        viewHolder.setIsRecyclable(false);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        if(viewHolder.getItemViewType()== LAYOUT_IMAGE) {
            ViewHolderImage holder = (ViewHolderImage) viewHolder;
            Picasso.get().load(textMessages.get(position).getText()).into(holder.messageText);
            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when clicked zoom in to image
                }
            });


        } else {
            ViewHolderText holder = (ViewHolderText) viewHolder;
            holder.messageText.setText(textMessages.get(position).getText());
            holder.MessageParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,textMessages.get(position).getText(), Toast.LENGTH_SHORT).show();
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
