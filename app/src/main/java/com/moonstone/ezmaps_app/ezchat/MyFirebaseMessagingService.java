package com.moonstone.ezmaps_app.ezchat;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moonstone.ezmaps_app.utilities.MyNotificationManager;

//allows acting on data in notifications, and displaying notifications when the app is foregrounded
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String deviceToken;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String s ="Empty";
        super.onNewToken(s);
        //record the device token so that FCM can send notifications to specific devices
        Log.d("Firebase", "MessageReceivedToken "+ FirebaseInstanceId.getInstance().getToken());
        Log.d("here we go", "NEW MESSAGE");


        //on receiving a notification while the app is foregrounded, construct and display the notification
        MyNotificationManager.getInstance(getApplicationContext())
                .displayNotification(s, s);

        //get the notification info
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        //text notification
        if(title.contains("Text from")){
            MyNotificationManager.getInstance(getApplicationContext())
                    .displayNotification(title, body);
        }
        //Calling notification receipt
        else{
            String room = remoteMessage.getData().get("room");
            String sender =remoteMessage.getData().get("sender");
            String callerPic = remoteMessage.getData().get("callerPic");
            incomingCall(sender, room, callerPic);
        }

    }

    //enter a call room on receiving an FCM call related notification
    private void incomingCall(String callerName, String roomId, String callerPic){
        Intent i = new Intent(this, incomingCall.class);
        i.putExtra("callerName",callerName );
        i.putExtra("roomId", roomId);
        i.putExtra("callerPic", callerPic);
        startActivity(i);
    }

    //get the devices FCM token
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
