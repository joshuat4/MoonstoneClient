package com.moonstone.ezmaps_app.ezdirection;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import android.view.View;
import android.widget.Toast;

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
        /*LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addTestProvider("Test", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        locationManager.setTestProviderEnabled("Test", true);

        // Set up your test

        Location location = new Location("Test");
        location.setLatitude(10.0);
        location.setLongitude(20.0);
        locationManager.setTestProviderLocation("Test", location);

        assertNotNull(location);

        // Check if your listener reacted the right way

        locationManager.removeTestProvider("Test");
*/



        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy( Criteria.ACCURACY_FINE );

        String mocLocationProvider = LocationManager.GPS_PROVIDER;//lm.getBestProvider( criteria, true );

        if ( mocLocationProvider == null ) {
            Toast.makeText(mActivity.getApplicationContext(), "No location provider found!", Toast.LENGTH_SHORT).show();
            return;
        }
        lm.addTestProvider(mocLocationProvider, false, false,
                false, false, true, true, true, 0, 5);
        lm.setTestProviderEnabled(mocLocationProvider, true);

        Location loc = new Location(mocLocationProvider);
        Location mockLocation = new Location(mocLocationProvider); // a string
        mockLocation.setLatitude(-26.902038);  // double
        mockLocation.setLongitude(-48.671337);
        mockLocation.setAltitude(loc.getAltitude());
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        lm.setTestProviderLocation( mocLocationProvider, mockLocation);
        //Toast.makeText(mActivity.getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();

        assertNotNull(loc);

    }






    // Call after test
    @After
    public void tearDown() throws Exception {

        mActivity = null;
    }


}