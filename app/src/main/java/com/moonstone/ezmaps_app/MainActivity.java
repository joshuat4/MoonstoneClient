package com.moonstone.ezmaps_app;

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
