package com.moonstone.ezmaps_app.utilities;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//this class is created to send out a http request async. to the app for ezdirections. The returned
//JSON is then returned as a JSONARRAY to the ezDirection class.
public class RetrieveFeed extends AsyncTask<String, Void, JSONArray> {
    private Exception exception;

    public interface AsyncResponse {
        void processFinish(JSONArray output);
    }

    public AsyncResponse delegate = null;

    public RetrieveFeed(AsyncResponse delegate){
        this.delegate = delegate;
    }



    //the actual async. process
    protected JSONArray doInBackground(String...urlString){

        try {
            //make the http request
            HttpURLConnection urlConnection = null;
            URL url = null;
            url = new URL(urlString[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            //read the actual response.
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            String jsonString = sb.toString();
            JSONArray reply = new JSONArray(jsonString);
            return reply;
        }
        catch (Exception e){
            this.exception = e;
            return null;
        }
    }

    //after response is received.
    protected void onPostExecute(JSONArray reply) {

        delegate.processFinish(reply);
    }

}
