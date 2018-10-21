package com.moonstone.ezmaps_app.qrcode;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.qrcode.QRCodeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class QRCodeActivityTest {

    @Rule
    public ActivityTestRule<QRCodeActivity> mActivityTestRule = new ActivityTestRule<>(QRCodeActivity.class);

    private QRCodeActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.QRCode);


        assertNotNull(a);


    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}