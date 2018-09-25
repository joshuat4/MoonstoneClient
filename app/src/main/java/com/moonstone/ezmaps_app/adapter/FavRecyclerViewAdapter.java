package com.moonstone.ezmaps_app;

import android.content.Context;
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

class FavRecyclerViewAdapter extends RecyclerView.Adapter<FavRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> favouriteList;
    private Context mContext;

    public FavRecyclerViewAdapter(ArrayList<String> favouriteList, Context mContext) {
        this.favouriteList = favouriteList;
        this.mContext = mContext;
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
        viewHolder.image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(mContext, favouriteList.get(i), Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.favImage);
            title = itemView.findViewById(R.id.favTitle);
        }
    }
}
