package com.moonstone.ezmaps_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);


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

        adapter = new TabAdapter(getSupportFragmentManager());

        // These are the tabs the Main Activity displays
        adapter.addFragment(new Tab1Fragment(), "Profile");
        adapter.addFragment(new Tab2Fragment(), "Home");
        adapter.addFragment(new Tab3Fragment(), "Contacts");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        tabLayout.setupWithViewPager(viewPager);
    }
}
