package com.moonstone.ezmaps_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FloatingActionButton returnToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String Uid = mAuth.getUid();

        Map<String, String> tokenID = new HashMap<>();
        tokenID.put("deviceToken", FirebaseInstanceId.getInstance().getToken());

        db.collection("users").document(Uid).set(tokenID, SetOptions.merge());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        returnToCall = findViewById(R.id.returnToCall);

        adapter = new TabAdapter(getSupportFragmentManager());

        // These are the tabs the Main Activity displays
        adapter.addFragment(new Tab1Fragment(), "Profile");
        adapter.addFragment(new Tab2Fragment(), "Home");
        adapter.addFragment(new Tab3Fragment(), "Contacts");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);


        returnToCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Calling.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume(){
        super.onResume();
        if(Calling.inCall){
            returnToCall.setVisibility(View.VISIBLE);
        }
        else{
            returnToCall.setVisibility(View.GONE);
        }
    }

}
