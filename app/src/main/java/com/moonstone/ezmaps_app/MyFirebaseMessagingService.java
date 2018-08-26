package com.moonstone.ezmaps_app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    public MyFirebaseMessagingService() {
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        MyNotificationManager.getInstance(getApplicationContext())
                .displayNotification(title, body);
    }
}
