package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ezdirection extends AppCompatActivity implements RetrieveFeed.AsyncResponse{
    private ArrayList<String> imageUrlsList;
    private ArrayList<String> textDirectionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezdirection);

        //get search address from search bar
        Intent intent = getIntent();
        String destination = intent.getStringExtra("destination");

        //execute async task
        new RetrieveFeed(this).execute("https://us-central1-it-project-moonstone-43019.cloudfunctions.net/mapRequest?text=145%20Queensberry%20Street,%20Carlton%20VIC---Student%20Village,%20The%20University%20of%20Melbourne%20Campus,%20Leicester%20Street,%20Carlton%20VIC");

    }

    @Override
    public void processFinish(JSONArray output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.;
        imageUrlsList = new ArrayList<>();
        textDirectionsList = new ArrayList<>();
        for(int i=0; i<output.length(); i ++){
            try {
                JSONObject object = output.getJSONObject(i);
                imageUrlsList.add(object.getString("imageURL"));
                textDirectionsList.add(object.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initRecyclerView();
    }





    private void initRecyclerView() {
        final String TAG = "initRecyclerView";
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(textDirectionsList, imageUrlsList, this);
        recyclerView.setAdapter(adapter);
    }


}
