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

    @Test
    public void testLaunch(){

        View a = mActivity.findViewById(R.id.recyclerView);
        View b = mActivity.findViewById(R.id.my_toolbar);
        View c = mActivity.findViewById(R.id.leftButton);
        View d = mActivity.findViewById(R.id.rightButton);
        View e = mActivity.findViewById(R.id.progressBar);
        View f = mActivity.findViewById(R.id.refreshButton);
        View g = mActivity.findViewById(R.id.contactsButton);
        View h = mActivity.findViewById(R.id.notFoundText);
        View i = mActivity.findViewById(R.id.notFoundSubtext);
        View j = mActivity.findViewById(R.id.notFoundImg);



        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);
        assertNotNull(e);
        assertNotNull(f);
        assertNotNull(g);
        assertNotNull(h);
        assertNotNull(i);
        assertNotNull(j);
    }



//    private LocationManager locationManager;
//
//    @Test
//    public void testGPS() {
//        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy( Criteria.ACCURACY_FINE );
//
//        String mocLocationProvider = LocationManager.GPS_PROVIDER;//lm.getBestProvider( criteria, true );
//
//        if ( mocLocationProvider == null ) {
//            Toast.makeText(mActivity.getApplicationContext(), "No location provider found!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        lm.addTestProvider(mocLocationProvider, false, false,
//                false, false, true, true, true, 0, 5);
//        lm.setTestProviderEnabled(mocLocationProvider, true);
//
//        Location loc = new Location(mocLocationProvider);
//        Location mockLocation = new Location(mocLocationProvider); // a string
//        mockLocation.setLatitude(-26.902038);  // double
//        mockLocation.setLongitude(-48.671337);
//        mockLocation.setAltitude(loc.getAltitude());
//        mockLocation.setTime(System.currentTimeMillis());
//        mockLocation.setAccuracy(1);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        }
//        lm.setTestProviderLocation( mocLocationProvider, mockLocation);
//        //Toast.makeText(mActivity.getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
//
//        assertNotNull(loc);
//
//    }






    // Call after test
    @After
    public void tearDown() throws Exception {

        mActivity = null;
    }


}