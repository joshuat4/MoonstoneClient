package com.moonstone.ezmaps_app.main;

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
import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezchat.Calling;
import com.moonstone.ezmaps_app.ezdirection.EZDirectionActivity;

import java.util.HashMap;
import java.util.Map;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FloatingActionButton returnToCall;
    private Button returnToNav;

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
        returnToNav = (Button) findViewById(R.id.returnToNav);
        returnToCall = findViewById(R.id.returnToCall);

        // Set Up initial activity
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Profile");
        adapter.addFragment(new Tab2Fragment(), "Home");
        adapter.addFragment(new Tab3Fragment(), "Contacts");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);


        if(getIntent().getExtras() != null){

            // If when starting an Activity there's frgToLoad
            if(getIntent().getExtras().containsKey("frgToLoad")) {

                int intentFragment = getIntent().getExtras().getInt("frgToLoad");

                setCurrentTab(intentFragment);
                addButtonToTop();


            }  else {
                returnToNav.setVisibility(View.GONE);
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }

        } else {
            returnToNav.setVisibility(View.GONE);
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }


        tabLayout.setupWithViewPager(viewPager);


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

    private void addButtonToTop(){

        returnToNav.setVisibility(View.VISIBLE);

        //return to navigation in its last saved state
        returnToNav.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), EZDirectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivityIfNeeded(intent, 0);

                returnToNav.setVisibility(View.GONE);

            }
        });



    }


    private void setCurrentTab(int intentFragment){
        switch (intentFragment) {
            case 1:
                viewPager.setCurrentItem(viewPager.getCurrentItem());
                break;
            case 2:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
            case 3:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
                break;
            default:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
        }
    }

    private static final boolean shouldAllowBack = false;
    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
        } else {
            super.onBackPressed();
        }
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

//    @SuppressLint("RestrictedApi")
//    @Override
//    public void onResume(){
//        super.onResume();
//        if(Calling.inCall){
//            returnToCall.setVisibility(View.VISIBLE);
//        }
//        else{
//            returnToCall.setVisibility(View.GONE);
//        }
//    }

}
