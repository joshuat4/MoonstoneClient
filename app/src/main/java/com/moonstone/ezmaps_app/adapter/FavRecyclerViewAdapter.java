package com.moonstone.ezmaps_app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class FavRecyclerViewAdapter extends RecyclerView.Adapter<FavRecyclerViewAdapter.ViewHolder> {

    private List<String> favouriteList;
    private Context mContext;

    private OnImageClickListener onImageClickListener;

    public FavRecyclerViewAdapter(ArrayList<String> favouriteList, Context mContext, OnImageClickListener onImageClickListener) {
        this.favouriteList = favouriteList;
        this.mContext = mContext;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row_fav, viewGroup, false);
        return new ViewHolder(view);
    }


    public String cleanUpName(String name) {

        return name.replace("%20"," ");
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        String url = "https://source.unsplash.com/1600x900/?" + favouriteList.get(i);

        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(viewHolder.image);


        viewHolder.title.setText(cleanUpName(favouriteList.get(i)));

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickListener.onImageClick(cleanUpName(favouriteList.get(i)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }


    public void filterOut(String filter) {
        final int size = favouriteList.size();
        for(int i = size - 1; i>= 0; i--) {
            if (!favouriteList.get(i).equals(filter)) {
                favouriteList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView title;
        private final Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            image = itemView.findViewById(R.id.favImage);
            title = itemView.findViewById(R.id.favTitle);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v){

            final Intent intent;

            Toast.makeText(mContext, "LAYOUT POSITION: " + getLayoutPosition(), Toast.LENGTH_SHORT).show();

        }
    }
}
