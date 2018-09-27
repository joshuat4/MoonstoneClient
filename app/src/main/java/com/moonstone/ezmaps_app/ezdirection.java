package com.moonstone.ezmaps_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.moonstone.ezmaps_app.RecyclerViewAdapter;


public class ezdirection extends AppCompatActivity implements RetrieveFeed.AsyncResponse, View.OnClickListener, LocationListener {
    private ArrayList<String> imageUrlsList;
    private ArrayList<String> textDirectionsList;

    private View recyclerView;

    private Toolbar toolbar;
    private ActionBar actionbar;

    private int counter = 0;
    private int numView;
    private RecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private ImageButton leftButton;
    private ImageButton rightButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private LocationManager locationManager;
    private double latitude, longitude;


    /* THE ONE USING RIGHTNOW */

    private Map<String, Object> tab2_to_ezdirection;
    private boolean isCurrentDestinationFavourited;
    private String currentDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezdirection);

        recyclerView = findViewById(R.id.recyclerView);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle("EZMap");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView((RecyclerView) recyclerView);

        //Get tab2_to_ezdirection item and resolve
        Intent intent = getIntent();
        tab2_to_ezdirection = (HashMap<String, Object>) intent.getSerializableExtra("tab2_to_ezdirection");
        isCurrentDestinationFavourited = (boolean) tab2_to_ezdirection.get("isCurrentDestinationFavourited");
        currentDestination = tab2_to_ezdirection.get("currentDestination").toString();

        Log.d("EZDIRECTION", "CURRENT DESTINATION RECEIVED FROM TAB2: " + currentDestination);


        // Using GPS to get current coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        // Prepare URL from current coordinates and currentDestination
        String url = "https://us-central1-it-project-moonstone-43019.cloudfunctions.net/mapRequest?text=";
        url += Double.toString(latitude) + "," + Double.toString(longitude) +  "---" + currentDestination.replaceAll(" ", "%20");
        Log.d("EZDIRECTION", "URL: " + url);


        //execute async task
        new RetrieveFeed(this).execute(url);

    }



    @Override
    public void finish() {
        Log.d("EZDIRECTION", "FINISH IS CALLED");

        Intent returnIntent = new Intent();
        returnIntent.putExtra("ezdirection_to_tab2", isCurrentDestinationFavourited);

        Log.d("EZDIRECTION", "ITEM IS PASSED Back to Tab 2");
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.rightButton:

                if(counter < numView - 1){
                    counter += 1;
                }

                layoutManager.scrollToPosition(counter);
                invalidateOptionsMenu();
                Log.d("EZDIRECTION", "SCROLL TO: " + counter + "/" + numView);

                break;
            case R.id.leftButton:

                if(counter >= 1){
                    counter -= 1;
                }

                layoutManager.scrollToPosition(counter);
                invalidateOptionsMenu();
                Log.d("EZDIRECTION", "SCROLL TO: " + counter + "/" + numView);
                break;
        }
    }



    @Override
    public void processFinish(JSONArray output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.;

        if (output != null) {
            imageUrlsList = new ArrayList<>();
            textDirectionsList = new ArrayList<>();
            for (int i = 0; i < output.length(); i++) {
                try {
                    JSONObject object = output.getJSONObject(i);
                    imageUrlsList.add(object.getString("imageURL"));
                    textDirectionsList.add(object.getString("description"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            initRecyclerView();
            invalidateOptionsMenu();
        }
        else{
            Intent intent = new Intent(this , error.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ezmap, menu);
        return true;
    }

    private void updateCounter(int newState){
        if(newState != -1){
            counter = newState;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.getItem(0).setVisible(true);
        menu.getItem(3).setVisible(true);

        menu.findItem(R.id.title).setTitle("(" + (counter + 1) + "/" + numView + ")");

        if(isCurrentDestinationFavourited){
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            Log.d("EZDIRECTION", "FAVOURITE TRUE");
        }else{
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
            Log.d("EZDIRECTION", "FAVOURITE FALSE");
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.favouriteFull){
            isCurrentDestinationFavourited = false;
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.favouriteEmpty){
            isCurrentDestinationFavourited = true;
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.options){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initRecyclerView() {
        final String TAG = "initRecyclerView";
        Log.d(TAG, "initRecyclerView: init recyclerview");

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(textDirectionsList, imageUrlsList, this);
        numView = adapter.getItemCount();

        recyclerView.setAdapter(adapter);
        layoutManager.setSmoothScrollbarEnabled(false);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                updateCounter(layoutManager.findLastCompletelyVisibleItemPosition());
                Log.d("EZDIRECTION", "SCROLL STATE CHANGED: " + layoutManager.findLastCompletelyVisibleItemPosition());

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d("EZDIRECTION", "LAST ITEM: " + layoutManager.findLastCompletelyVisibleItemPosition());

            }
        });


    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
