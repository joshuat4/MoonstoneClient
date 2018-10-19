package com.moonstone.ezmaps_app.ezchat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;


import com.google.firebase.auth.FirebaseAuth;
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.utilities.RetrieveFeed;

import org.json.JSONArray;

import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.Constants;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class Calling extends AppCompatActivity implements RetrieveFeed.AsyncResponse{

    private RtcEngine myRtcEngine;
    private FirebaseAuth mAuth;
    private String toUserId;

    private FrameLayout remoteContainer;
    private FrameLayout localContainer;

    private ImageButton switchCamera;
    private ImageButton audioMode;
    private ImageButton endCall;
    private ImageButton minimiseCall;

    private CircleImageView callerPic;
    private TextView callerName;
    private String roomId;
    private PulsatorLayout pulsator;
    private MediaPlayer mMediaPlayer;

    private Boolean recieveMode = false;
    public static Boolean inCall = false;

    private IRtcEngineEventHandler myRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);

                    if(mMediaPlayer != null){
                        mMediaPlayer.stop();

                    }

                    localContainer.setVisibility(View.VISIBLE);
                    remoteContainer.setVisibility(View.VISIBLE);
                    pulsator.stop();
                    callerPic.setVisibility(View.INVISIBLE);
                    callerName.setVisibility(View.INVISIBLE);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        Log.d("callingmy", "remote video set up2a");

        if (remoteContainer.getChildCount() >= 1) {
            return;
        }



        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());




        Log.d("callingmy", "remote video set up2cc");
        remoteContainer.addView(surfaceView);
        Log.d("callingmy", "remote video set up2");
        myRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid);
    //new LongOperation().execute(Integer.toString(uid));
}

    private void notifyRecipient(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            toUserId = extras.getString("toUserId");
        }

        String url = "https://us-central1-it-project-moonstone-43019.cloudfunctions.net/callNotification2?text=" + mAuth.getCurrentUser().getDisplayName()+ "---" + toUserId  +  "---" + roomId ;
        //execute async task
        new RetrieveFeed(this).execute(url);

    }

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_ui);
        mAuth = FirebaseAuth.getInstance();

        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }

        inCall = true;

        //Setup room id
        roomId = "MoonstoneCallRoom:" + Integer.toString(new Random().nextInt(100000) + 1);

        localContainer = findViewById(R.id.frontCameraContainer);
        remoteContainer =  findViewById(R.id.remote_video_view_container);
        Log.d("callingmy", "initiallising");

        switchCamera = findViewById(R.id.switch_camera);
        audioMode = findViewById(R.id.mic_button);
        audioMode.setTag("audio");
        endCall = findViewById(R.id.end_call);
        callerName = findViewById(R.id.callerName);
        callerPic = findViewById(R.id.callerPic);
        minimiseCall = findViewById(R.id.minimiseCall);
        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);

        //Get data passed through from ContactRecyclerViewAdapter
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callerName.setText(extras.getString("name"));
            if(extras.getString("roomId")!=null){
                roomId = extras.getString("roomId");
                recieveMode= true;
            }
        }

        initializeRtcEngine();

        if(!recieveMode){
            //Starting a new call from contacts
            localContainer.setVisibility(View.INVISIBLE);
            remoteContainer.setVisibility(View.INVISIBLE);
            callerPic.setVisibility(View.VISIBLE);
            callerName.setVisibility(View.VISIBLE);
            //The current device is the calling device
            notifyRecipient();
            pulsator.start();
            playSound();
        }


        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRtcEngine.switchCamera();
            }
        });

        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRtcEngine.leaveChannel();
                if(mMediaPlayer != null){
                    mMediaPlayer.stop();
                }

                inCall = false;
                finish();
            }
        });

        //Background the call
        minimiseCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        audioMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ok", audioMode.getTag().toString());
                //Go to video mode
                if (audioMode.getTag().toString().equals("video_cam")) {
                    myRtcEngine.enableVideo();
                    localContainer.setVisibility(View.VISIBLE);
                    remoteContainer.setVisibility(View.VISIBLE);

                    callerPic.setVisibility(View.INVISIBLE);
                    callerName.setVisibility(View.INVISIBLE);
                    audioMode.setImageResource(R.drawable.microphone);
                    audioMode.setTag("audio");
                }
                //Go to audio mode
                else{
                    myRtcEngine.disableVideo();
                    localContainer.setVisibility(View.INVISIBLE);
                    remoteContainer.setVisibility(View.INVISIBLE);

                    callerPic.setVisibility(View.VISIBLE);
                    callerName.setVisibility(View.VISIBLE);
                    audioMode.setImageResource(R.drawable.ic_videocamon_black_24dp);
                    audioMode.setTag("video_cam");
                }


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted

                } else {
                    myRtcEngine.leaveChannel();
                    mMediaPlayer.stop();
                    inCall = false;
                    finish();
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //permissions
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 101);
    }

    private void setupVideoProfile() {

        myRtcEngine.enableAudio();
        myRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_240P_3, false);
    }


    private void initializeRtcEngine(){
        try {
            myRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id),myRtcEventHandler);
            joinRoom();
            localVideoConfig();
            setupVideoProfile();
            myRtcEngine.startPreview();
            Log.d("callingmy", "does this work asda s");
        } catch (Exception e) {
            Log.d("callingmy", "does this work");
            e.printStackTrace();
        }
    }

    private void joinRoom() {
        myRtcEngine.enableVideo();
        myRtcEngine.joinChannel(null, roomId, null, new Random().nextInt(100000)+1);
    }

    //Assign front camera to little framelayout
    private void localVideoConfig(){
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());

        surfaceView.setZOrderMediaOverlay(true);
        localContainer.addView(surfaceView);

        myRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        myRtcEngine.leaveChannel();
        mMediaPlayer.stop();
        inCall = false;
        super.onBackPressed();
    }



    @Override
    public void processFinish(JSONArray output){

    }


    private void playSound(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }
}




