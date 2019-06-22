package me.jonlin.android.climate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity
{
    // Constants:
    // i.e.: api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=672568ea088e0bd3e7531e72fd524787
    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // test URL.
    // final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=672568ea088e0bd3e7531e72fd524787";

  // App ID to use OpenWeather data
    private final String APP_ID = "672568ea088e0bd3e7531e72fd524787";
    // Time between location updates (5000 milliseconds or 5 seconds)
    private final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    private final float MIN_DISTANCE = 1000;
    private final int LOC_PERM_REQUEST_CODE = 1;
    //Location Provider
    //fine location. If using coarse location use network_provider because cell tower || wifi network
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    private TextView mCityLabel;
    private ImageView mWeatherImage;
    private TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    // component that will start or stop request location updates.
    /*
    if needs to update every 10 minute. make a 10 minute timer that asynchrousnly updates???wg
     */
    // TODO: if location permission is off what does this return?
    LocationManager mLocationManager;
    // component listens for changes/updates
    LocationListener mLocationListener = new LocationListener()
    {
        // update the weather when location changes.
        @Override
        public void onLocationChanged(Location location)
        {
            log("onLocationChanged callback ");
            String latStr = Double.toString(location.getLatitude());
            String longStr = Double.toString(location.getLongitude());
            log("lat: " + latStr + "| long: " + longStr);
            //api query
            RequestParams rp = new RequestParams("lat", latStr, "long", longStr, "appid", APP_ID);
            getRequestWeather(rp);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {
            log("onStatusChanged callback ");
        }

        @Override
        public void onProviderEnabled(String s)
        {
            log("onProviderEnabled callback");
        }

        @Override
        public void onProviderDisabled(String s)
        {
            log("onProviderDisabled callback");
            //disable network / gps. i.e. wifi, location, airplane mnode
        }
        //min time between updates, min distance between updates.
    };

    private void log(String str)
    {
        Log.d(this.getClass().getSimpleName(), str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //TODO: how to handle versions below marshmallow 6.0. OR just target marshmallo 6.0+
        //setting up listener
        if (ActivityCompat.checkSelfPermission(getBaseContext(),  Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //setup dialog
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //prompt for request location of user. GPS enabled.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERM_REQUEST_CODE);
            return;
        }

        log("!!!133 come here? ever>?");
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);


        // TODO: Add an OnClickListener to the changeCityButton here:

    }

    // location service permission is granted from operating system callback.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        log("onRequestPermissionResult() call");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if requesting location without permission, app will crash.
        if(requestCode == LOC_PERM_REQUEST_CODE)
        {
            //must contain one element. Hardcode that assume only location permission is requested.
            //result of permissions[0] = grantResults[0]
            // location request granted. Another way to check permission instead of using AppCompat. Manifest.
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                log("onRequestPermissionResult: permission granted");
                // not compile error. Runtime error complaint however it is handled in the if statement.
                mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
            }
            else
            {
                // prompt dialog if deny because app wont work as intended.
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERM_REQUEST_CODE);
                log("permission denied");
            }
        }
    }


    // TODO: Add onResume() here:

    @Override
    protected void onResume()
    {
        super.onResume();
        log("onResume called");
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        log("onStop called");
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void getRequestWeather(RequestParams rp)
    {
        log("getRequestWeather() called");
        // uses a background thread to send requests. A request always is followed by a response
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, rp, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
               log("Sucess. JSON: " + response.toString());
               // TODO: parse response string to display to UI.
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse)
            {
                // inside anonymous class WeatherController.this
                Log.e(this.getClass().getSimpleName(), "Fail " + throwable.toString());
                log("Status code " + statusCode);

                Toast.makeText(getBaseContext(), "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
          //?q=London,uk&APPID=672568ea088e0bd3e7531e72fd524787

    }



    // TODO: Add updateUI() here:



    // TODO: Add onPause() here:

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart()");
        /*
        //snippet https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/locationmanager.html#TaskVerifyProvider

        // This verification should be done during onStart() because the system calls
        // this method when the user returns to the activity, which ensures the desired
        // location provider is enabled each time the activity resumes from the stopped state.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
        }
        */
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

}
