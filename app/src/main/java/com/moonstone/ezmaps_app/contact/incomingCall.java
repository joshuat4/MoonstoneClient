package com.moonstone.ezmaps_app.contact;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moonstone.ezmaps_app.R;

public class incomingCall extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private ImageButton accept, decline;
    private TextView callerName;
    private String callerString;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.temp);
        Log.d("nononono", "What about here");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callerString= extras.getString("callerName");
            roomId = extras.getString("roomId");
        }
        Log.d("nononono", "What about here2");

        accept = findViewById(R.id.acceptCall);
        decline = findViewById(R.id.declineCall);
        callerName = findViewById(R.id.callerName);

        callerName.setText(callerString);

        Log.d("nononono", "????");

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                finish();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Calling.class);
                i.putExtra("name",callerString );
                i.putExtra("roomId", roomId);
                mMediaPlayer.stop();
                startActivity(i);
            }
        });

        Log.d("nononono", "What about here 2121");
        playSound();
    }


    private void playSound(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        Log.d("nononono", "What about here xxxx");
        mMediaPlayer.start();
    }
}
