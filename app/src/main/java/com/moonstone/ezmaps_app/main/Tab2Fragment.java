package com.moonstone.ezmaps_app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezdirection.EZDirectionActivity;
import com.moonstone.ezmaps_app.ezdirection.FavRecyclerViewAdapter;
import com.moonstone.ezmaps_app.ezdirection.PlaceAutocompleteAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Tab2Fragment extends Fragment  implements FavRecyclerViewAdapter.ListItemClickListener,
    GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private ImageButton button;
    private AutoCompleteTextView source;
    private ImageView image;
    private ImageButton clearButton;

    private LinearLayoutManager layoutManager;
    private FavRecyclerViewAdapter adapter;
    private PlaceAutocompleteAdapter placeAdapter;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentDestination;
    private ArrayList<String> favouritePlaces;
    RecyclerView favRecyclerView;
    private boolean isCurrentDestinationFavourited;

    private int REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds( new LatLng(-44, 107), new LatLng(-15,158));
    private View view;

    private boolean ezdirectionInSession = false;
    private TextWatcher textWatcher;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_two, container, false);

            image = (ImageView) view.findViewById(R.id.image);
            clearButton = (ImageButton) view.findViewById(R.id.clearButton);
            source = (AutoCompleteTextView) view.findViewById(R.id.searchBar);
            button = (ImageButton) view.findViewById(R.id.searchButton);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            // Get Favourite Places from Firestore
            loadFavouritePlaces(view);

            Picasso.get()
                    .load("https://source.unsplash.com/collection/1980117/1600x900")
                    .into(image);

            image.setColorFilter(ContextCompat.getColor(getContext(), R.color.tblack));

            clearButton.setVisibility(View.GONE);
            clearButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    source.getText().clear();
                    clearButton.setVisibility(View.GONE);
                }
            });

            ///////////////////////autocomplete stuff//////////////////////////////////////////////


            mGoogleApiClient = new GoogleApiClient
                .Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this.getActivity(), this)
                .build();

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("AU")
                .build();

            placeAdapter = new PlaceAutocompleteAdapter(this.getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS, typeFilter);


            source.setAdapter(placeAdapter);
            /////////////////////////////////////////////////////////////////////////////////////

            textWatcher = new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().isEmpty()){
                        clearButton.setVisibility(View.GONE);
                    }else{
                        clearButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

            };


            source.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            currentDestination = source.getText().toString().trim();
                            startEZMap(currentDestination);
                            return true;
                        }
                    }
                    return false;
                }
            });


            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Get the current destination typed into the search field
                    currentDestination = source.getText().toString().trim();
                    startEZMap(currentDestination);
                }
            });

        source.setSelected(false);

        return view;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        currentDestination = favouritePlaces.get(clickedItemIndex);
        startEZMap(currentDestination);

    }

    // Load favourite places from Cloud Firestore
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

    // Initialise Favourite Recycler View
    public void initFavRecyclerView(View view, ArrayList<String> favouritePlaces){
        layoutManager = new GridLayoutManager(getActivity(), 2);
        favRecyclerView = view.findViewById(R.id.favRecyclerView);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView((RecyclerView) favRecyclerView);

        favRecyclerView.setLayoutManager(layoutManager);
        adapter = new FavRecyclerViewAdapter(favouritePlaces, getActivity(), this);
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

    @Override
    public void onResume(){
        super.onResume();
        source.addTextChangedListener(textWatcher);
    }


    public void startEZMap(String destination){
        if(!ezdirectionInSession){
            HashMap<String, Object> tab2_to_ezdirection = new HashMap<String, Object>();
            tab2_to_ezdirection.put("currentDestination", destination);

            if(isCurrentDestinationFavourited(destination)){
                tab2_to_ezdirection.put("isCurrentDestinationFavourited", true);

            }else{
                tab2_to_ezdirection.put("isCurrentDestinationFavourited", false);

            }

            ezdirectionInSession = true;
            Log.d("TAB2", "EZDirection is in session: " + ezdirectionInSession);

            // Set Result
            Intent intent = new Intent(this.getActivity(), EZDirectionActivity.class);
            intent.putExtra("tab2_to_ezdirection", tab2_to_ezdirection);
            startActivityForResult(intent, REQUEST_CODE);

        }
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
        Log.d("TAB2", "Request Code: " + requestCode);
        Log.d("TAB2", "Result Code: " + resultCode);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            boolean ezdirection_to_tab2 = data.getBooleanExtra("ezdirection_to_tab2", false);

            Log.d("TAB2", "Passed item received: " + ezdirection_to_tab2);

            if(ezdirection_to_tab2 && !isCurrentDestinationFavourited){
                Log.d("TAB2", "Add current favourite place");
                addCurrentDestinationToFavouritePlace();

            }else if(!ezdirection_to_tab2 && isCurrentDestinationFavourited){
                Log.d("TAB2", "Remove current favourite place");
                removeCurrentDestinationToFavouritePlace();
            }


        }else{
            Log.d("TAB2", " ");
            Log.d("TAB2", "Passed item not received");

        }

        ezdirectionInSession = false;
        Log.d("TAB2", "EZDirection session is over: " + ezdirectionInSession);
    }

    private void addCurrentDestinationToFavouritePlace(){

        // Locally
        int index;

        if(currentDestination != null){
            if(adapter != null){
                index = adapter.getItemCount();
                favouritePlaces.add(currentDestination);
                adapter.notifyItemInserted(index);

            }else{
                index = 0;
                favouritePlaces.add(currentDestination);
                initFavRecyclerView(view, favouritePlaces);
            }

            Log.d("TAB2/addD", "Index: " + index + " currentDest: " + currentDestination + " favPlaces: " + favouritePlaces);
            updateFavouritePlacesToDB(favouritePlaces);
        }

    }

    private void removeCurrentDestinationToFavouritePlace(){
        Log.d("TAB2/rm", "CURRENT: " + currentDestination + " FAV PLACES: " + favouritePlaces);
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
            if(comparePlaces(item, dest)){
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
