package com.moonstone.ezmaps_app.contact;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class FriendRequestActivityTest {

    @Rule
    public ActivityTestRule<FriendRequestActivity> mActivityTestRule = new ActivityTestRule<>(FriendRequestActivity.class);

    private FriendRequestActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View toolbar = mActivity.findViewById(R.id.my_toolbar);
        View requestRecyclerView = mActivity.findViewById(R.id.requestRecyclerView);

        assertNotNull(toolbar);
        assertNotNull(requestRecyclerView);
    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}