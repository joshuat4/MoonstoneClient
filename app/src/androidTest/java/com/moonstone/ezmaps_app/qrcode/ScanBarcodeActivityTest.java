package com.moonstone.ezmaps_app.qrcode;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.qrcode.ScanBarcodeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScanBarcodeActivityTest {
    @Rule
    public ActivityTestRule<ScanBarcodeActivity> mActivityTestRule = new ActivityTestRule<>(ScanBarcodeActivity.class);

    private ScanBarcodeActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.camera_preview);
        View b = mActivity.findViewById(R.id.exitButton);

        assertNotNull(a);
        assertNotNull(b);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}