package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.moonstone.ezmaps_app.FavRecyclerViewAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Tab2Fragment extends Fragment {
    private ImageButton button;
    private EditText source;
    private ImageView image;
    private ImageButton clearButton;

    private LinearLayoutManager layoutManager;
    private FavRecyclerViewAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            clearButton = (ImageButton) view.findViewById(R.id.clearButton);
            source = (EditText) view.findViewById(R.id.searchBar);
            button = (ImageButton) view.findViewById(R.id.searchButton);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            loadFavouritePlaces(view);

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


    public void loadFavouritePlaces(final View view){

        final String Uid = mAuth.getUid();

        final DocumentReference docRef = db.collection("users").document(Uid);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAB2", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    ArrayList<String> favouritePlaces = (ArrayList<String>) snapshot.get("favouritePlaces");

                    if(!favouritePlaces.isEmpty()){
                        initFavRecyclerView(view, favouritePlaces);
                    }

                    Log.d("TAB2", "Current data: " + snapshot.getData());
                } else {
                    Log.d("TAB2", "Current data: null");
                }
            }
        });


    }



    public void initFavRecyclerView(View view, ArrayList<String> favouritePlaces){

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView favRecyclerView = view.findViewById(R.id.favRecyclerView);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView((RecyclerView) favRecyclerView);

        favRecyclerView.setLayoutManager(layoutManager);
        adapter = new FavRecyclerViewAdapter(favouritePlaces, getActivity());
        favRecyclerView.setAdapter(adapter);

        Log.d("TAB2", "INITIALISE FAV RECYCLER");

        favRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int ydy = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                Log.d("TAB2", "SCROLL STATE CHANGED: " + layoutManager.findLastCompletelyVisibleItemPosition());

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d("TAB2", "LAST ITEM: " + layoutManager.findLastCompletelyVisibleItemPosition());

            }
        });


    }


    public void startEZMap(){
        Intent intent = new Intent(Tab2Fragment.this.getActivity(), ezdirection.class);
        String destination = source.getText().toString().trim();
        intent.putExtra("destination", destination);
        startActivity(intent);

    }


}
