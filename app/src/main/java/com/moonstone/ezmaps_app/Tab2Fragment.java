package com.moonstone.ezmaps_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.moonstone.ezmaps_app.FavRecyclerViewAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab2Fragment extends Fragment {

    private ImageButton button;
    private EditText source;
    private ImageView image;
    private ImageButton clearButton;

    private LinearLayoutManager layoutManager;
    private FavRecyclerViewAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentDestination;
    private ArrayList<String> favouritePlaces;
    RecyclerView favRecyclerView;
    private boolean isCurrentDestinationFavourited;

    private int REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            clearButton = (ImageButton) view.findViewById(R.id.clearButton);
            source = (EditText) view.findViewById(R.id.searchBar);
            button = (ImageButton) view.findViewById(R.id.searchButton);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            // Get Favourite Places from Firestore
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

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<String> list = (ArrayList<String>) document.get("favouritePlaces");
                        favouritePlaces = new ArrayList<>();
                        favouritePlaces.addAll(list);

                        Log.d("TAB2/init", "Favourite places received: " + favouritePlaces);

                        // If Fav Places is not Empty, open up a recycler list of fav places
                        if(!favouritePlaces.isEmpty()){
                            Log.d("TAB2", "FAVOURITE PLACE NOT EMPTY, LOAD Recycler View");
                            initFavRecyclerView(view, favouritePlaces);
                        }

                    } else {
                        Log.d("TAB2", "No such document");
                    }
                } else {
                    Log.d("TAB2", "get failed with ", task.getException());
                }
            }
        });

    }


    public void initFavRecyclerView(View view, ArrayList<String> favouritePlaces){
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        favRecyclerView = view.findViewById(R.id.favRecyclerView);

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

        HashMap<String, Object> tab2_to_ezdirection = new HashMap<String, Object>();

        // Get the current destination typed into the search field
        currentDestination = source.getText().toString().trim();
        tab2_to_ezdirection.put("currentDestination", currentDestination);

        // Check if the current destination is favourited
        if(isCurrentDestinationFavourited(currentDestination)){

            tab2_to_ezdirection.put("isCurrentDestinationFavourited", true);

        }else{

            tab2_to_ezdirection.put("isCurrentDestinationFavourited", false);

        }

        // Send the hashmap (tab2_to_ezdirection) to EZDirection
        Intent intent = new Intent(this.getActivity(), ezdirection.class);
        intent.putExtra("tab2_to_ezdirection", tab2_to_ezdirection);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private boolean isCurrentDestinationFavourited(String currentDestination) {
        for(String places : favouritePlaces){
            if(comparePlaces(places, currentDestination)){
                isCurrentDestinationFavourited = true;
                return true;
            }
        }

        isCurrentDestinationFavourited = false;
        return false;
    }

    private boolean comparePlaces(String place1, String place2){
        return place1.toUpperCase().equals(place2.toUpperCase());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("TAB2", "Activity is returned");
        Log.d("TAB2", "REQ CODE: " + requestCode);
        Log.d("TAB2", "RES CODE: " + resultCode);
        Log.d("TAB2", "DATA: " + (boolean) data.getExtras().get("ezdirection_to_tab2"));

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("TAB2", "REQUEST CODE RECEIVED");

            boolean ezdirection_to_tab2 = (boolean) data.getExtras().get("ezdirection_to_tab2");

            Log.d("TAB2", "PASSED ITEM RECEIVED: " + ezdirection_to_tab2);

            // Current Destination was Not Favourited but Favourited during EZ Direction
            if(ezdirection_to_tab2 && !isCurrentDestinationFavourited){
                Log.d("TAB2", "ADD CURRENT FAV Place");
                addCurrentDestinationToFavouritePlace();

            }else if(!ezdirection_to_tab2 && isCurrentDestinationFavourited){
                Log.d("TAB2", "REMOVE CURRENT FAV Place");
                removeCurrentDestinationToFavouritePlace();
            }


        }
    }

    private void addCurrentDestinationToFavouritePlace(){

        // Locally
        int index = adapter.getItemCount();
        favouritePlaces.add(index, currentDestination);
        adapter.notifyItemInserted(index);
        Log.d("TAB2/addD", "Index: " + index + " currentDest: " + currentDestination + " favPlaces: " + favouritePlaces);

        updateFavouritePlacesToDB(favouritePlaces);

    }

    private void removeCurrentDestinationToFavouritePlace(){

        int index = getIndex(currentDestination, favouritePlaces);
        if(index >= 0){
            favouritePlaces.remove(index);
            adapter.notifyItemRemoved(index);
            Log.d("TAB2/rmD", "Index: " + index + " currentDest: " + currentDestination + " favPlaces: " + favouritePlaces);

            updateFavouritePlacesToDB(favouritePlaces);
        }

    }




    private int getIndex(String dest, ArrayList<String> list){
        int index = 0;

        for (String item : list){
            if(comparePlaces(item, currentDestination)){

                return index;
            }
            index++;
        }

        return -1;
    }


    private void updateFavouritePlacesToDB(ArrayList<String> list){
        final String Uid = mAuth.getUid();
        DocumentReference docRef = db.collection("users").document(Uid);
        docRef
                .update("favouritePlaces", list)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                        Log.d("TAB2/updateDB", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAB2/updateDB", "Error updating document", e);
                    }
                });

    }






}
