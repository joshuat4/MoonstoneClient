package com.moonstone.ezmaps_app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String deviceToken;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String s ="Empty";
        super.onNewToken(s);
//        Log.d("Token", "onMessageReceived: "+s);
        Log.d("Firebase", "MessageReceivedToken "+ FirebaseInstanceId.getInstance().getToken());
//        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getInstanceId());



        MyNotificationManager.getInstance(getApplicationContext())
                .displayNotification(s, s);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        MyNotificationManager.getInstance(getApplicationContext())
                .displayNotification(title, body);

    }
    public static String getDeviceToken() {
        return deviceToken;
    }

    public static void setDeviceToken(String s) {
        deviceToken = s;
    }


}
