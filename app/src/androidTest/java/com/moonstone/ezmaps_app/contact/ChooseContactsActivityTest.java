package com.moonstone.ezmaps_app.contact;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChooseContactsActivityTest {

    @Rule
    public ActivityTestRule<ChooseContactsActivity>  mActivityTestRule = new ActivityTestRule<>(ChooseContactsActivity.class);

    private ChooseContactsActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View contactFilter = mActivity.findViewById(R.id.contactFilter);
        View contactLoading = mActivity.findViewById(R.id.contactsLoading);
        View contactClear = mActivity.findViewById(R.id.clearButton);
        View toolbar = mActivity.findViewById(R.id.my_toolbar);
        View contactRecyclerView = mActivity.findViewById(R.id.contactRecyclerView);

        assertNotNull(contactClear);
        assertNotNull(contactFilter);
        assertNotNull(contactLoading);
        assertNotNull(toolbar);
        assertNotNull(contactRecyclerView);
    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}