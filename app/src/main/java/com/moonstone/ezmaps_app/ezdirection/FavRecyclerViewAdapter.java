package com.moonstone.ezmaps_app.ezdirection;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;
import java.util.List;

public class FavRecyclerViewAdapter extends RecyclerView.Adapter<FavRecyclerViewAdapter.ViewHolder>{

    private List<String> favouriteList;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    public FavRecyclerViewAdapter(ArrayList<String> favouriteList, Context mContext, ListItemClickListener listener) {
        this.favouriteList = favouriteList;
        this.mContext = mContext;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row_fav, viewGroup, false);
        return new ViewHolder(view);
    }


    public String cleanUpName(String name) {
        int commaIndex = name.indexOf(',');
        name.replace("%20"," ");

        String placeholder = name;
        if(commaIndex != -1){
            placeholder = name.substring(0,commaIndex);
        }

        return placeholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String url = "https://source.unsplash.com/1600x900/?" + favouriteList.get(i);
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(viewHolder.image);

        viewHolder.title.setText(cleanUpName(favouriteList.get(i)));
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
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
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
            Toast.makeText(mContext, "LAYOUT POSITION: " + getLayoutPosition(), Toast.LENGTH_SHORT).show();

        }
    }
}
