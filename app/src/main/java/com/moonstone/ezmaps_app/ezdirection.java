package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


public class ezdirection extends AppCompatActivity implements RetrieveFeed.AsyncResponse, View.OnClickListener{
    private ArrayList<String> imageUrlsList;
    private ArrayList<String> textDirectionsList;

    private View recyclerView;

    private Toolbar toolbar;
    private ActionBar actionbar;

    private int counter = 0;
    private int numView;
    private static boolean isCurrentDestinationFavourited;
    private RecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private ImageButton leftButton;
    private ImageButton rightButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static String currentDestination;

    /* THE ONE USING RIGHTNOW */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String url = new String();
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

        //Get Current Destination
        Intent intent = getIntent();
        setCurrentDestination(intent.getStringExtra("destination").replaceAll(" ", "%20"));

        // Check if Current Destination is in Favourite Places
        isCurrentDestinationFavourited(getCurrentDestination());
        Log.d("EZDIRECTION", getCurrentDestination());

        url = "https://us-central1-it-project-moonstone-43019.cloudfunctions.net/mapRequest?text=145%20Queensberry%20Street,%20Carlton%20VIC---" + getCurrentDestination();

        //execute async task
        new RetrieveFeed(this).execute(url);

    }

    private static void setIsCurrentDestinationFavourited(boolean b){
        isCurrentDestinationFavourited = b;
    }

    private void isCurrentDestinationFavourited(String destination){
        final String Uid = mAuth.getUid();

        DocumentReference docRef = db.collection("users").document(Uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> favouritePlaces = (ArrayList<String>) document.get("favouritePlaces");

                        for (String place: favouritePlaces){
                            if(place.equals(ezdirection.getCurrentDestination())){
                                Log.d("EZDIRECTION", "CURRENT DESTINATION WAS FAVED");
                                ezdirection.setIsCurrentDestinationFavourited(true);
                            }
                        }


                        Log.d("EZDIRECTION", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("EZDIRECTION", "No such document");
                    }
                } else {
                    Log.d("EZDIRECTION", "get failed with ", task.getException());
                }
            }
        });

        return;
    }


    private static String getCurrentDestination(){
        return currentDestination;
    }

    private static void setCurrentDestination(String d){
        currentDestination = d;
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
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("passed_item", isCurrentDestinationFavourited);
        // setResult(RESULT_OK);
        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
        super.finish();
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
            setIsCurrentDestinationFavourited(false);
            // removeCurrentFavouritePlace();
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.favouriteEmpty){
            setIsCurrentDestinationFavourited(true);
            // addCurrentFavouritePlace();
            invalidateOptionsMenu();
            return true;
        }

        if(id == R.id.options){
            //Something options for sharing
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


}
