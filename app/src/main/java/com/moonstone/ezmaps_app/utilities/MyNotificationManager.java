package com.moonstone.ezmaps_app.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.moonstone.ezmaps_app.contact.Constants;
import com.moonstone.ezmaps_app.ezchat.Calling;
import com.moonstone.ezmaps_app.main.MainActivity;
import com.moonstone.ezmaps_app.R;

import java.util.ArrayList;

//allows notification receiving and building
public class MyNotificationManager  {
    private Context mCtx;
    private static MyNotificationManager mInstance;
    private Intent intent;

    private MyNotificationManager (Context context){
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    //construct and display a notification
    public void displayNotification(String title, String body){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(body);


        //launch main activity on pressing notification
        if(title.contains("Text from")){
            intent = new Intent(mCtx, MainActivity.class);
        }
        //Calling notification
        else{
            intent = new Intent(mCtx, Calling.class);

        }


        //construct and display notification
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mNotificationManager != null){
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

}
