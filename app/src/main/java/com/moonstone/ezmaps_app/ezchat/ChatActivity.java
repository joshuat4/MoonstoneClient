package com.moonstone.ezmaps_app.ezchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezprofile.ImageUploadActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//the activity wherein instant messaging takes place
public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private MessageRecyclerViewAdapter adapter;
    //facilitates updating the recycler view without reinitialising it
    private boolean notFirstTime = false;

    private EditText textField; //where message is typed
    private ImageButton sendButton;
    private ImageButton cameraButton;
    private static Toolbar toolbar;
    private static ActionBar actionbar;
    public static ProgressBar messagesLoading;

    private static String toUserID; //user being sent to
    private static String fromUserID; //this user
    private String userName; //to user's name
    private String userProfilePic; //to user's profile pic
    private Timestamp currentTime; //current time

    //where the text messages will be stored
    private ArrayList<EzMessage> ezMessagesArray = new ArrayList<>();

    public static void setFromUserID(String s) {
        fromUserID = s;
    }

    public String getToUserID() {
        return toUserID;
    }

    Handler handler = new Handler();

    public static void setToUserID(String s) {
        Log.d("testing", "user id set as" + s);
        toUserID = s;
    }

    public int REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textField = findViewById(R.id.textField);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);

        messagesLoading = findViewById(R.id.messagesLoading);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);

            }
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentTime = Timestamp.now();
        Log.d("time", "time: " + currentTime.toDate().toString());


        Bundle extras = getIntent().getExtras();
        Boolean fromChooseContacts;
        ArrayList<String> currentImageUrlsList;
        int currentCounter;

        //load in to user's name and pic, and whether from choose contacts
        if (extras != null) {
            userName = extras.getString("name");
            userProfilePic = extras.getString("picture");
            fromChooseContacts = extras.getBoolean("fromChooseContacts");

            //send images in EZDirection navigation
            if(fromChooseContacts){
                currentImageUrlsList = extras.getStringArrayList("currentImageUrlsList");
                currentCounter = extras.getInt("currentCounter");

                if(currentCounter == -1){
                    sendImage(mAuth.getUid(),
                            currentImageUrlsList, toUserID,
                            Timestamp.now().toDate().toString());

                }else{
                    sendImage(mAuth.getUid(),
                            currentImageUrlsList.get(currentCounter), toUserID,
                            Timestamp.now().toDate().toString());
                }

            }
        }

        actionbar.setTitle(userName);

        //instantiate and update the chat
        handler.post(updateView);

        //SEND MESSAGE
        sendButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                if(textField.getText().toString().length() > 1){
                    sendText(mAuth.getUid(),
                            textField.getText().toString(),
                            toUserID,
                            Timestamp.now().toDate().toString());

                }

                textField.setText("");
            }
        });

        //send pic
        cameraButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("CHAT ACTIVITY", "CAMERA BUTTON");
                startActivityForResult(new Intent(ChatActivity.this, ImageSendingActivity.class), REQUEST_CODE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ChatActivity", "Activity is returned");
        Log.d("ChatActivity", "Request Code: " + requestCode + "/" + REQUEST_CODE);
        Log.d("ChatActivity", "Result Code: " + resultCode + "/" + RESULT_OK);

        //send an image
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            String imageUrl = data.getStringExtra("imageUrl");
            Log.d("ChatActivity", "IMAGE URL:" + imageUrl);

            if(imageUrl != null){
                sendImage(mAuth.getUid(), imageUrl, toUserID,
                        Timestamp.now().toDate().toString());

                Toast.makeText(ChatActivity.this, "Sending Image Successful", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ChatActivity.this, "Sending Image Unsuccessful", Toast.LENGTH_LONG).show();
            }

        }
    }


    private void sendImage(String from, ArrayList<String> imageUrlList, String to, String time){
        final Map<String, Object> message = new HashMap<>();

        //construct the message
        for(String imageUrl : imageUrlList){
            message.put("toUserId", to);
            message.put("text", imageUrl);
            message.put("fromUserId", from);
            message.put("time", time);
            message.put("textType", "IMAGE");

            //put it in the database
            db.collection("users").document(from).collection("contacts").document(to).collection("messages").add(message);
            db.collection("users").document(to).collection("contacts").document(from).collection("messages").add(message);

        }

        Log.d("messages", "Image Sent");

    }

    private void sendImage(String from, String imageUrl, String to, String time){
        final Map<String, Object> message = new HashMap<>();

        //construct message
        message.put("toUserId", to);
        message.put("text", imageUrl);
        message.put("fromUserId", from);
        message.put("time", time);
        message.put("textType", "IMAGE");

        //put in db
        db.collection("users").document(from).collection("contacts").document(to).collection("messages").add(message);
        db.collection("users").document(to).collection("contacts").document(from).collection("messages").add(message);

        Log.d("messages", "Image Sent");
    }

    private void sendText(String from, String text, String to, String time){
        final Map<String, Object> message = new HashMap<>();
        //construct message
        message.put("toUserId", to);
        message.put("text", text);
        message.put("fromUserId", from);
        message.put("time", time);
        message.put("textType", "TEXT");

        //put in db
        db.collection("users").document(from).collection("contacts").document(to).collection("messages").add(message);
        db.collection("users").document(to).collection("contacts").document(from).collection("messages").add(message);
        Log.d("messages", "Message written: " + text);
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //where data is fetched from the firestore, and fed into the recyclerview
    private void loadDataFromFirebase() {
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
                                    DateFormat dateFormat = new SimpleDateFormat(
                                            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.UK);

                                    try {
                                        Date date = dateFormat.parse(dc.getDocument().getString("time"));
                                        //add all the messages into ezMessagesArray
                                        ezMessagesArray.add(new EzMessage(dc.getDocument().getString("text"),
                                                dc.getDocument().getString("toUserId"),
                                                dc.getDocument().getString("fromUserId"),date,
                                                dc.getDocument().getString("textType")));

                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                        Log.d("messages", "date failed");
                                    }

                                   Log.d("messages", "new doc: " + dc.getDocument().getString("text"));
                                   newDocs++;
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                        //sort by time and display the messages if there are any
                        if(newDocs>0){
                           ezMessagesArray.sort(new EzMessagesComparator());
                           if(notFirstTime){
                               adapter.notifyDataSetChanged();
                              RecyclerView rv = findViewById(R.id.messageRecyclerView);
                              rv.scrollToPosition(ezMessagesArray.size() - 1);
                           } else if(ezMessagesArray.size() > 0) {
                               ezMessagesArray.sort(new EzMessagesComparator());
                               initRecyclerView();
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
            i.putExtra("picture", userProfilePic);
            i.putExtra("toUserId", toUserID);
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