package com.moonstone.ezmaps_app.main;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.viewPager);
        View b = mActivity.findViewById(R.id.tabLayout);
        View c = mActivity.findViewById(R.id.returnToNav);
        View d = mActivity.findViewById(R.id.returnToCall);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}