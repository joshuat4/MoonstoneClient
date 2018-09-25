package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Tab2Fragment extends Fragment {
    private ImageButton button;
    private EditText source;
    private ImageView image;
    private ImageButton clearButton;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            clearButton = (ImageButton) view.findViewById(R.id.clearButton);
            source = (EditText) view.findViewById(R.id.searchBar);
            button = (ImageButton) view.findViewById(R.id.searchButton);

            Picasso.get()
                    .load("https://source.unsplash.com/collection/1980117/1600x900")
                    .into(image);

            image.setColorFilter(ContextCompat.getColor(getContext(), R.color.tblack));

            clearButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    source.getText().clear();
                    clearButton.setVisibility(View.GONE);
                }
            });


            source.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("TAB2", "SEARCH TYPED IN");
                    clearButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

            });

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


            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    startEZMap();
                }
            });

        source.clearFocus();

        return view;
    }




    public void startEZMap(){
        Intent intent = new Intent(Tab2Fragment.this.getActivity(), ezdirection.class);
        String destination = source.getText().toString().trim();
        intent.putExtra("destination", destination);
        startActivity(intent);

    }


}
