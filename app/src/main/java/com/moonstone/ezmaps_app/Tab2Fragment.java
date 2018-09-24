package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Tab2Fragment extends Fragment {
    private ImageButton button;
    private EditText source;
    private ImageView image;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            Picasso.get()
                    .load("https://source.unsplash.com/WLUHO9A_xik/1600x900")
                    .into(image);
            image.setColorFilter(ContextCompat.getColor(getContext(), R.color.tblack));

            source = (EditText) view.findViewById(R.id.searchBar);
            source.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        startEZMap();

                        return true;
                    }
                    return false;
                }
            });

            button = (ImageButton) view.findViewById(R.id.searchButton);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    startEZMap();
                }
            });

        return view;
    }



    public void startEZMap(){
        Intent intent = new Intent(Tab2Fragment.this.getActivity(), ezdirection.class);
        String destination = source.getText().toString().trim();
        intent.putExtra("destination", destination);
        startActivity(intent);

    }


}
