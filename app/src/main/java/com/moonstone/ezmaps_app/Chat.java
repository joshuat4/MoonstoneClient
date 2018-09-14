package com.moonstone.ezmaps_app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
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
    public static ProgressBar messagesLoading;
    private static final String TEST_CHILD = "testing";

    private static String toUserID;
    private static String fromUserID;

    private ArrayList<String> text = new ArrayList<>();
    private ArrayList<String> from = new ArrayList<>();
    private ArrayList<String> to = new ArrayList<>();

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
        Log.d("HERE", "CHAT INITIALISED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        textField = findViewById(R.id.textField);
        sendButton = findViewById(R.id.sendButton);
        messagesLoading = findViewById(R.id.messagesLoading);


        //Filter code
//        textField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                filter(s.toString());
//            }
//        });

        loadDataFromFirebase();
//                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        final int size = task.getResult().size();
//                        for (QueryDocumentSnapshot message : task.getResult()) {
//                            String messageId = message.getId();
//                            db.collection("messages").document(messageId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    text.add(documentSnapshot.get("text").toString());
//                                    from.add(documentSnapshot.get("fromUserId").toString());
//                                    to.add(documentSnapshot.get("toUserId").toString());
//                                    adapter.refreshData();
//                                    if(text.size() == size){
//                                        messagesLoading.setVisibility(View.GONE);
//                                        initRecyclerView();
//                                        notFirstTime = true;
//                                    }
//                                }
//                            });
//                            Log.d("SUCCESS", "contactId:" + messageId);
//                        }
//                    } else {
//                        Log.d("FAILURE", "Error getting documents: ", task.getException());
//                    }
//                }
//            });



        //SEND MESSAGE
        sendButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final String Uid = mAuth.getUid();
                // Add a new document with a generated id.
                final Map<String, Object> message = new HashMap<>();
                message.put("toUserId", toUserID);
                message.put("text", textField.getText().toString());
                message.put("fromUserId", Uid);
                final Map<String, Object> nullContent = new HashMap<>();
                message.put("k", "teamSnapchat");

                db.collection("messages")
                        .add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                db.collection("users").document(Uid).collection("contacts").document(toUserID).collection("messages").document(documentReference.getId()).set(nullContent);
                                db.collection("users").document(toUserID).collection("contacts").document(Uid).collection("messages").document(documentReference.getId()).set(nullContent);
                                Log.d("SUCCESS", "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("FAILURE", "Error adding document", e);
                            }
                        });
                textField.setText("");
            }
        });
    }

    //Sets up the recycler view
    private void initRecyclerView(){
        RecyclerView recyclerView =  findViewById(R.id.messageRecyclerView);
        Log.d("HERE", text.toString());
        adapter = new MessageRecyclerViewAdapter(this, text);
        recyclerView.setAdapter(adapter) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    private void filter(String text){
//
//        //Filtered arrays
//        ArrayList<String> ftext = new ArrayList<>();
//
//        int counter = 0;
//
//        for(String message : text){
//            if(message.toLowerCase().contains(text.toLowerCase())){
//                ftext.add(text.get());
//            }
//            counter += 1;
//        }
//        adapter.filterList(fmessages);
//    }

    private void refresh(){
        Log.d("HERE", "refresh");
//        adapter.clear();

//        adapter.refreshData();
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
                            final String docId = querySnapshot.getId();
                            db.collection("messages").document(docId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                            DocumentSnapshot doc = task2.getResult();
                                            text.add(doc.getString("name"));
                                            from.add(doc.getString("profilePic"));
                                            to.add(doc.getString("profilePic"));
                                        }
                                    });
                        }
                        messagesLoading.setVisibility(View.GONE);
                        initRecyclerView();
                        notFirstTime = true;
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

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            refresh();
//        }
//    }
}