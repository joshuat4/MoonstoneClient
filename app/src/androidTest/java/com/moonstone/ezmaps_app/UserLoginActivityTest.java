package com.moonstone.ezmaps_app;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.moonstone.ezmaps_app.UserLoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserLoginActivityTest {
    @Rule
    public ActivityTestRule<UserLoginActivity> mActivityTestRule = new ActivityTestRule<>(UserLoginActivity.class);

    private UserLoginActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.emailField);
        View b = mActivity.findViewById(R.id.passwordField);
        View c = mActivity.findViewById(R.id.loginButton);
        View d = mActivity.findViewById(R.id.textViewSignUp);
        View e = mActivity.findViewById(R.id.progressBar);

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