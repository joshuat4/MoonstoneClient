package com.moonstone.ezmaps_app.contact;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewContactSearchActivityTest {
    @Rule
    public ActivityTestRule<NewContactSearchActivity> mActivityTestRule = new ActivityTestRule<>(NewContactSearchActivity.class);

    private NewContactSearchActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.findRecyclerView);
        View b = mActivity.findViewById(R.id.filterAllContacts);
        View c = mActivity.findViewById(R.id.searchButton);
        View d = mActivity.findViewById(R.id.my_toolbar);
        View e = mActivity.findViewById(R.id.clearButton);
        View f = mActivity.findViewById(R.id.findContactsLoading);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertNotNull(e);
        assertNotNull(f);
    }




    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

}