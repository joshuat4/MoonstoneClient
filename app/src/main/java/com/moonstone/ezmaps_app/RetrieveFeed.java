import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RetrieveFeed extends AsyncTask<String, Void, JSONObject> {
    private Exception exception;

    @Override
    protected JSONObject doInBackground(String...urlString){

        try {
            HttpURLConnection urlConnection = null;
            URL url = null;
            url = new URL(urlString[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            String jsonString = sb.toString();
            System.out.println("JSON: " + jsonString);
            JSONObject reply = new JSONObject(jsonString);
            return reply;
        }
        catch (Exception e){
            this.exception = e;
            return null;
        }
    }
}
