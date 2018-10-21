package com.moonstone.ezmaps_app;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.FrontPageActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class FrontPageActivityTest {
    @Rule
    public ActivityTestRule<FrontPageActivity> mActivityTestRule = new ActivityTestRule<>(FrontPageActivity.class);

    private FrontPageActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.loginButton);
        View b = mActivity.findViewById(R.id.signUpButton);

        assertNotNull(a);
        assertNotNull(b);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}