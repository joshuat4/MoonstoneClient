package com.moonstone.ezmaps_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class ChatActivity extends AppCompatActivity {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        //ImageView messageImageView;
        TextView senderTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageText);
//            messageImageView = (ImageView) itemView.findViewById(R.id.messageImage);
            senderTextView = (TextView) itemView.findViewById(R.id.senderText);
        }
    }

    private static final String TEST_CHILD = "testing";
    private DatabaseReference mFirebaseDatabaseReference;
    private RecyclerView mMessageRecyclerView;
    private FirebaseRecyclerAdapter<EzMessage, MessageViewHolder> mFirebaseAdapter;

    private ImageButton mSendButton;
    private EditText mMessageEditText;
    private LinearLayoutManager mLinearLayoutManager;

    private static String toUserID;
    private static String fromUserID;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SnapshotParser<EzMessage> parser = new SnapshotParser<EzMessage>() {
            @Override
            public EzMessage parseSnapshot(DataSnapshot dataSnapshot) {
                EzMessage ezMessage = dataSnapshot.getValue(EzMessage.class);
                if (ezMessage != null) {
                    ezMessage.setMessageId(dataSnapshot.getKey());
                }
                return ezMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(TEST_CHILD);

        FirebaseRecyclerOptions<EzMessage> options =
                new FirebaseRecyclerOptions.Builder<EzMessage>()
                        .setQuery(messagesRef, parser)
                        .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.getDescription();
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);

            mChannel.setVibrationPattern(new long[] {1000, 100});

            mNotificationManager.createNotificationChannel(mChannel);
        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<EzMessage, MessageViewHolder>(options) {

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            EzMessage ezMessage) {

                if (ezMessage.getText() != null) {
                    viewHolder.messageTextView.setText(ezMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
//                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
//                } else if (ezMessage.getAttachmentUrl() != null) {
//                    String imageUrl = ezMessage.getImageUrl();
//                    if (imageUrl.startsWith("gs://")) {
//                        StorageReference storageReference = FirebaseStorage.getInstance()
//                                .getReferenceFromUrl(imageUrl);
//                        storageReference.getDownloadUrl().addOnCompleteListener(
//                                new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.isSuccessful()) {
//                                    String downloadUrl = task.getResult().toString();
//                                    Glide.with(viewHolder.messageImageView.getContext())
//                                            .load(downloadUrl)
//                                            .into(viewHolder.messageImageView);
//                                } else {
//                                    Log.w(TAG, "Getting download url was not successful.",
//                                            task.getException());
//                                }
//                            }
//                        });
//                } else {
//                    Glide.with(viewHolder.messageImageView.getContext())
//                            .load(ezMessage.getAttachmentUrl()Url())
//                            .into(viewHolder.messageImageView);
                }
//                viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                viewHolder.messageTextView.setVisibility(TextView.GONE);
                viewHolder.senderTextView.setText(ezMessage.getFromUserId());
            }

            // log a view action on it
//                FirebaseUserActions.getInstance().end(getMessageViewAction(ezMessage));
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int ezMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (ezMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.textField);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseDatabaseReference.child(TEST_CHILD).push().setValue(new EzMessage(mMessageEditText.getText().toString(),toUserID,fromUserID));
                mMessageEditText.setText("");
            }
        });


//        adapter = new TabAdapter(getSupportFragmentManager());
//
//        // These are the tabs the Main Activity displays
//        adapter.addFragment(new Tab1Fragment(), "Profile");
//        adapter.addFragment(new Tab2Fragment(), "Home");
//        adapter.addFragment(new Tab3Fragment(), "Contacts");
//
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//        tabLayout.setupWithViewPager(viewPager);
    }
}