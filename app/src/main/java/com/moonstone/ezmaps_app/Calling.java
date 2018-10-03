package com.moonstone.ezmaps_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

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

    private IRtcEngineEventHandler myRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
//            setupRemoteVideo(uid);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.d("callingmy", "remote video set up");
            setupRemoteVideo(uid);
            super.onUserJoined(uid, elapsed);
        }
    };

    private void setupRemoteVideo(int uid) {
        Log.d("callingmy", "remote video set up2a");
        FrameLayout container =  (FrameLayout)findViewById(R.id.remote_video_view_container);
        Log.d("callingmy", "remote video set up2f");
        Log.d("callingmy", "remote video set up2ff");



        //This is the problem line


        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);




        Log.d("callingmy", "remote video set up2cc");
        container.addView(surfaceView);
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
        Log.d("callingmy", "initiallising");

        initializeRtcEngine();
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
        FrameLayout container = findViewById(R.id.frontCameraContainer);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());

        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);

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




