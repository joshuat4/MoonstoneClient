package com.moonstone.ezmaps_app.ezprofile;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezprofile.UploadActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class UploadActivityTest {
    @Rule
    public ActivityTestRule<UploadActivity> mActivityTestRule = new ActivityTestRule<>(UploadActivity.class);

    private UploadActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.cancelButton);
        View b = mActivity.findViewById(R.id.uploadButton);
        View c = mActivity.findViewById(R.id.imageView);
        View d = mActivity.findViewById(R.id.progressBar);
        View e = mActivity.findViewById(R.id.my_toolbar);


        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertNotNull(e);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}