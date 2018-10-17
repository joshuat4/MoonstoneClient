package com.moonstone.ezmaps_app.ezdirection;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import android.view.View;

import com.moonstone.ezmaps_app.R;

import static android.support.v4.content.ContextCompat.getSystemService;
import static org.junit.Assert.*;


public class EZDirectionActivityTest  {



    // Specify that this activity is launched
    @Rule
    public ActivityTestRule<EZDirectionActivity> mActivityTestRule = new ActivityTestRule<EZDirectionActivity>(EZDirectionActivity.class);

    private EZDirectionActivity mActivity = null;

    // Call before test case
    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();

    }



    private LocationManager locationManager;

    @Test
    public void testGPS() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addTestProvider("Test", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled("Test", true);

        /*// Set up your test

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(GPS_MOCK_PROVIDER)) {
            locationManager.requestLocationUpdates(GPS_MOCK_PROVIDER, 0, 0f, this);
        }

        // Long 144.96, Lat -37.7962
        Location location = new Location("Test");
        location.setLatitude(-37.7962);
        location.setLongitude(144.96);
        locationManager.setTestProviderLocation("Test", location);
*/
        // Check if your listener reacted the right way
        //assertNotNull(location);

        locationManager.removeTestProvider("Test");
    }


    // Call after test
    @After
    public void tearDown() throws Exception {

        mActivity = null;
    }


}