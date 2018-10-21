package com.moonstone.ezmaps_app.ezchat;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class incomingCallTest {
    @Rule
    public ActivityTestRule<incomingCall> mActivityTestRule = new ActivityTestRule<>(incomingCall.class);

    private incomingCall mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.acceptCall);
        View b = mActivity.findViewById(R.id.declineCall);
        View c = mActivity.findViewById(R.id.callerName);
        View d = mActivity.findViewById(R.id.callerPic);

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