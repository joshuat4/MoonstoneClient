package com.moonstone.ezmaps_app.contact;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String deviceToken;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String s ="Empty";
        super.onNewToken(s);
//      Log.d("Token", "onMessageReceived: "+s);
        Log.d("Firebase", "MessageReceivedToken "+ FirebaseInstanceId.getInstance().getToken());
        Log.d("here we go", "NEW MESSAGE");
//        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getInstanceId());



        MyNotificationManager.getInstance(getApplicationContext())
                .displayNotification(s, s);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();


        if(title.contains("Text from")){
            MyNotificationManager.getInstance(getApplicationContext())
                    .displayNotification(title, body);
        }
        //Calling notification
        else{
            incomingCall(title, body);
        }

    }

    private void incomingCall(String callerName, String roomId){
        Intent i = new Intent(this, incomingCall.class);
        Log.d("aaaa", callerName);
        Log.d("aaaa", roomId);
        i.putExtra("callerName",callerName );
        i.putExtra("roomId", roomId);
        startActivity(i);
    }

    public static String fetchToken(){
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        return deviceToken;
    }

    public static String getDeviceToken() {
        return deviceToken;
    }

    public static void setDeviceToken(String s) {
        deviceToken = s;
    }


}
