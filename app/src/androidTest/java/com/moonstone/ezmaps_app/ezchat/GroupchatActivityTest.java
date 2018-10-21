package com.moonstone.ezmaps_app.ezchat;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class GroupchatActivityTest {
    @Rule
    public ActivityTestRule<GroupchatActivity> mActivityTestRule = new ActivityTestRule<>(GroupchatActivity.class);

    private GroupchatActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.textField);
        View b = mActivity.findViewById(R.id.sendButton);
        View c = mActivity.findViewById(R.id.cameraButton);
        View d = mActivity.findViewById(R.id.messagesLoading);
        View e = mActivity.findViewById(R.id.my_toolbar);
        View f = mActivity.findViewById(R.id.groupMessageRecyclerView);
        View g = mActivity.findViewById(R.id.action_call);


        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertNotNull(e);
        assertNotNull(f);
        assertNotNull(g);

    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }



}