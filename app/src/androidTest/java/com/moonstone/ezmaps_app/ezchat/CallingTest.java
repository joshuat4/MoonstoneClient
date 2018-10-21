package com.moonstone.ezmaps_app.ezchat;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezchat.Calling;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class CallingTest {

    @Rule
    public ActivityTestRule<Calling> mActivityTestRule = new ActivityTestRule<>(Calling.class);

    private Calling mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.frontCameraContainer);
        View b = mActivity.findViewById(R.id.remote_video_view_container);
        View c = mActivity.findViewById(R.id.switch_camera);
        View d = mActivity.findViewById(R.id.mic_button);
        View e = mActivity.findViewById(R.id.end_call);
        View f = mActivity.findViewById(R.id.callerName);
        View g = mActivity.findViewById(R.id.callerPic);
        View h = mActivity.findViewById(R.id.minimiseCall);
        View i = mActivity.findViewById(R.id.pulsator);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertNotNull(e);
        assertNotNull(f);
        assertNotNull(g);
        assertNotNull(h);
        assertNotNull(i);
    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }



}