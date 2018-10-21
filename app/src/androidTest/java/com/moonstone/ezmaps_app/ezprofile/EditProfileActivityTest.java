package com.moonstone.ezmaps_app.ezprofile;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.R;
import com.moonstone.ezmaps_app.ezprofile.EditProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class EditProfileActivityTest {
    @Rule
    public ActivityTestRule<EditProfileActivity> mActivityTestRule = new ActivityTestRule<>(EditProfileActivity.class);

    private EditProfileActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.editName);
        View b = mActivity.findViewById(R.id.editEmail);
        View c = mActivity.findViewById(R.id.editEmailLayout);
        View d = mActivity.findViewById(R.id.editPassword);
        View e = mActivity.findViewById(R.id.editProfilePic);
        View f = mActivity.findViewById(R.id.toolbar);
        View g = mActivity.findViewById(R.id.progressBar);
        View h = mActivity.findViewById(R.id.editImage);
        View i = mActivity.findViewById(R.id.signOutButton);

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