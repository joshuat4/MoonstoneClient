package com.moonstone.ezmaps_app;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.moonstone.ezmaps_app.adapter.MessageRecyclerViewAdapter;

//the activity wherein instant messaging takes place
public class Chat extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private MessageRecyclerViewAdapter adapter;
    //facilitates updating the recycler view without reinitialising it
    private boolean notFirstTime = false;

    private EditText textField;
    private ImageButton sendButton;
    private static Toolbar toolbar;
    private static ActionBar actionbar;
    public static ProgressBar messagesLoading;

    private static String toUserID;
    private static String fromUserID;
    private String userName;
    private Timestamp currentTime;

    //where the text messages will be stored
    private ArrayList<EzMessage> ezMessagesArray = new ArrayList<>();

    public static void setFromUserID(String s) {
        fromUserID = s;
    }

    Handler handler = new Handler();

    public static void setToUserID(String s) {
        toUserID = s;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        currentTime = Timestamp.now();
        Log.d("time", "time: " + currentTime.toDate().toString());

        //Get data passed through from ContactRecyclerViewAdapter
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("name");
        }

        Log.d("HERE", "CHAT INITIALISED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        textField = (EditText) findViewById(R.id.textField);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        messagesLoading = findViewById(R.id.messagesLoading);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setTitle(userName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //instantiate and update the chat
        handler.post(updateView);


        //SEND MESSAGE
        sendButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                currentTime = Timestamp.now();
                final String Uid = mAuth.getUid();
                // Add a new document with a generated id.
                final Map<String, Object> message = new HashMap<>();

                //Add to recycler view
                message.put("toUserId", toUserID);
                message.put("text", textField.getText().toString());
                message.put("fromUserId", Uid);
                message.put("time", currentTime.toDate().toString());

                //Add to map for actual database
                db.collection("users").document(Uid).collection("contacts").document(toUserID).collection("messages").add(message);
                db.collection("users").document(toUserID).collection("contacts").document(Uid).collection("messages").add(message);
                Log.d("messages", "Message written");

                textField.setText("");
            }
        });
    }

    //Sets up the recycler view
    private void initRecyclerView(){
        RecyclerView recyclerView =  findViewById(R.id.messageRecyclerView);
        adapter = new MessageRecyclerViewAdapter(this, ezMessagesArray);
        recyclerView.setAdapter(adapter) ;
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
    }

    private void refresh(){
        Log.d("messages", "refresh");
        final String Uid = mAuth.getUid();
        adapter.clear();
        loadDataFromFirebase();
        adapter.refreshData();
    }


    @Override
    public void onResume(){
        super.onResume();
        //other stuff
    }

    //allow the loading process to be run periodically
    private final Runnable updateView = new Runnable(){
        public void run(){
            try {
                loadDataFromFirebase();
//                handler.postDelayed(this, 5000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //where data is fetched from the firestore, and fed into the recyclerview
    private void loadDataFromFirebase() {
        Log.d("HERE", "just in ldff");
        messagesLoading.setVisibility(View.VISIBLE);
        final String Uid = mAuth.getUid();

        //get all the messages for the currrently selected contact
        db.collection("users").document(Uid).collection("contacts").document(toUserID).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        int newDocs = 0;
                        //get any messages not already loaded
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    ezMessagesArray.add(new EzMessage(dc.getDocument().getString("text"),
                                           dc.getDocument().getString("toUserId"),
                                           dc.getDocument().getString("fromUserId"),
                                           new Date(dc.getDocument().getString("time"))));
                                   Log.d("messages", "new doc: " + dc.getDocument().getString("text"));
                                   newDocs++;
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                        //sot and display the messages if there are any
                        if(newDocs>0){
                           ezMessagesArray.sort(new EzMessagesComparator());
                           if(notFirstTime){
                               adapter.notifyDataSetChanged();
                              RecyclerView rv = findViewById(R.id.messageRecyclerView);
                              rv.scrollToPosition(ezMessagesArray.size() - 1);
                           } else if(ezMessagesArray.size() > 0) {
                               ezMessagesArray.sort(new EzMessagesComparator());
                               initRecyclerView();
                               Log.d("messages", "2");
                               notFirstTime = true;
                               adapter.notifyDataSetChanged();
                           }
                       }


                    }
                });

        messagesLoading.setVisibility(View.GONE);
    }

    //Toolbar stuff

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call) {
            Intent i = new Intent(getApplicationContext(), Calling.class);
            i.putExtra("name",userName );
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateView);
    }


}