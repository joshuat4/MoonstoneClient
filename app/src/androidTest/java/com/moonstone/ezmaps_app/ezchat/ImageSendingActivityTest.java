package com.moonstone.ezmaps_app.ezchat;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImageSendingActivityTest {

    @Rule
    public ActivityTestRule<ImageSendingActivity> mActivityTestRule = new ActivityTestRule<>(ImageSendingActivity.class);

    private ImageSendingActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.cancelButton);
        View b = mActivity.findViewById(R.id.uploadButton);
        View d = mActivity.findViewById(R.id.progressBar);
        View e = mActivity.findViewById(R.id.my_toolbar);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(d);
        assertNotNull(e);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}