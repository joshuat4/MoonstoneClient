package com.moonstone.ezmaps_app.ezdirection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;

public class EZCardRecyclerViewAdapter extends RecyclerView.Adapter<EZCardRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "EZCardRecyclerViewAdapter";
    //vars
    private ArrayList<String> textDirections = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private Context mContext;


    public EZCardRecyclerViewAdapter(ArrayList<String> textDirections, ArrayList<String> imageUrls, Context mContext) {
        this.textDirections = textDirections;
        this.imageUrls = imageUrls;
        this.mContext = mContext;
    }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         Log.d(TAG, "onCreateViewHolder: called");
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.direction_cards, viewGroup, false);
         return new ViewHolder(view);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
         Log.d(TAG, "onBindViewHolder: called");

         Glide.with(mContext)
                 .asBitmap()
                 .load(imageUrls.get(i))
                 .into(viewHolder.image);

         viewHolder.name.setText(textDirections.get(i));

         viewHolder.image.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 Log.d(TAG, "onClick: clicked on image: " + textDirections.get(i));
                 Toast.makeText(mContext, textDirections.get(i), Toast.LENGTH_SHORT).show();
             }
         });

     }

     @Override
     public int getItemCount() {
         return imageUrls.size();
     }

     public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageDirections);
            name = itemView.findViewById(R.id.textDirections);
        }
    }
}
