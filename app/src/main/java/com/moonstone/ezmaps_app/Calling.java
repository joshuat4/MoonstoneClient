package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;


import com.google.android.gms.vision.Frame;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Random;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class Calling extends AppCompatActivity {

    private RtcEngine myRtcEngine;
    private FirebaseAuth mAuth;

    private FrameLayout remoteContainer;
    private FrameLayout localContainer;

    private ImageButton switchCamera;
    private ImageButton audioMode;
    private ImageButton endCall;

    private CircleImageView callerPic;
    private TextView callerName;



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
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
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

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_ui);
        mAuth = FirebaseAuth.getInstance();
        localContainer = findViewById(R.id.frontCameraContainer);
        remoteContainer =  findViewById(R.id.remote_video_view_container);
        Log.d("callingmy", "initiallising");
        initializeRtcEngine();

        switchCamera = findViewById(R.id.switch_camera);
        audioMode = findViewById(R.id.mic_button);
        endCall = findViewById(R.id.end_call);

        callerName = findViewById(R.id.callerName);
        callerPic = findViewById(R.id.callerPic);

        //Get data passed through from ContactRecyclerViewAdapter
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callerName.setText(extras.getString("name"));
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
                finish();
            }
        });

        audioMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (audioMode.getTag().equals(R.drawable.video_camera)) {
                    myRtcEngine.enableVideo();
                    localContainer.setVisibility(View.VISIBLE);
                    remoteContainer.setVisibility(View.VISIBLE);

                    callerPic.setVisibility(View.INVISIBLE);
                    callerName.setVisibility(View.INVISIBLE);
                    audioMode.setImageResource(R.drawable.microphone);
                }
                else{
                    myRtcEngine.disableVideo();
                    localContainer.setVisibility(View.INVISIBLE);
                    remoteContainer.setVisibility(View.INVISIBLE);

                    callerPic.setVisibility(View.VISIBLE);
                    callerName.setVisibility(View.VISIBLE);
                    audioMode.setImageResource(R.drawable.videocamera);
                }


            }
        });

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
        myRtcEngine.joinChannel(null, "IT-Project-Moonstone:room1", null, new Random().nextInt(100000)+1);
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
        myRtcEngine.leaveChannel();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        myRtcEngine.leaveChannel();
        super.onBackPressed();
    }


    private class LongOperation extends AsyncTask<String, Void, String> {
        private FrameLayout container;
        private int uid;
        private SurfaceView surfaceView;
        @Override
        protected String doInBackground(String... params) {
            uid = Integer.parseInt(params[0]);
            FrameLayout container =  (FrameLayout)findViewById(R.id.remote_video_view_container);
            SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
            return "Executed ";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("callingmy", "remote video set up2cc");
            container.addView(surfaceView);
            Log.d("callingmy", "remote video set up2");
            myRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

            surfaceView.setTag(uid);
        }

        @Override
        protected void onPreExecute() {}

    }



}




