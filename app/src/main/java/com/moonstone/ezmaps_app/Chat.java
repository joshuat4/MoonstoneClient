package com.moonstone.ezmaps_app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import butterknife.ButterKnife;

public class Chat extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private MessageRecyclerViewAdapter adapter;
    private boolean notFirstTime = false;

    private EditText textField;
    private Button sendButton;
    private static Toolbar toolbar;
    private static ActionBar actionbar;
    public static ProgressBar messagesLoading;
    private static final String TEST_CHILD = "testing";

    private static String toUserID;
    private static String fromUserID;
    private String userName;
    private Timestamp currentTime;

    private ArrayList<String> text = new ArrayList<>();
    private ArrayList<String> from = new ArrayList<>();
    private ArrayList<String> to = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<EzMessage> ezMessagesArray = new ArrayList<>();

    public String getFromUserID() {
        return fromUserID;
    }

    public static void setFromUserID(String s) {
        fromUserID = s;
    }



    public String getToUserID() {
        return toUserID;
    }

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

        textField = findViewById(R.id.textField);
        sendButton = findViewById(R.id.sendButton);
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

//        loadDataFromFirebase();


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
        Log.d("HERE", text.toString());
        adapter = new MessageRecyclerViewAdapter(this, ezMessagesArray);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refresh(){
        Log.d("messages", "refresh");
        final String Uid = mAuth.getUid();
        db.collection("users").document(Uid).collection("contacts").document(toUserID).collection("messages").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   for (DocumentChange dc : task.getResult().getDocumentChanges()) {
                       switch (dc.getType()) {
                           case ADDED:
                               System.out.println("New city: " + dc.getDocument().getData());
                               break;
                           case MODIFIED:
                               System.out.println("Modified city: " + dc.getDocument().getData());
                               break;
                           case REMOVED:
                               System.out.println("Removed city: " + dc.getDocument().getData());
                               break;
                           default:
                               break;
                       }
                   }
               }
           });
        adapter.refreshData();
        loadDataFromFirebase();
    }


    @Override
    public void onResume(){
        refresh();
        super.onResume();
        //other stuff
    }

    private void loadDataFromFirebase() {
        messagesLoading.setVisibility(View.VISIBLE);
        if (text.size() > 0) {
            text.clear();
            from.clear();
            to.clear();
        }
        final String Uid = mAuth.getUid();
        Log.d("HERE", "please don't run");
        db.collection("users").document(Uid).collection("contacts").document(toUserID).collection("messages").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {
//
                            Date date = new Date(querySnapshot.getString("time"));
                            ezMessagesArray.add(new EzMessage(querySnapshot.getString("text"),
                                                        querySnapshot.getString("toUserId"),
                                                        querySnapshot.getString("fromUserId"),
                                                        date));

                        }
                        ezMessagesArray.sort(new EzMessagesComparator());
                        messagesLoading.setVisibility(View.GONE);
                        Log.d("messages", "1");
                        if(ezMessagesArray.size() > 0) {
                            if (!notFirstTime) {
                                initRecyclerView();
                                Log.d("messages", "2");
                                notFirstTime = true;
                            } else {
                                Log.d("messages", "3");
                                adapter.clear();
                                adapter.refreshData();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Chat.this, "FAIL", Toast.LENGTH_SHORT).show();
                        Log.d(
                                "FAILURE IN CHAT", e.getMessage());
                    }
                });
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
            Toast.makeText(Chat.this, "Call pressed", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}