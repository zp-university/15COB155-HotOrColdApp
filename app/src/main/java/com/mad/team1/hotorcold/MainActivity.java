package com.mad.team1.hotorcold;

import android.Manifest;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.mad.team1.hotorcold.api.Game;
import com.mad.team1.hotorcold.api.GameManager;


public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private static final GameManager gameManager = GameManager.createGameManager();
    private static final int  READ_LOCATION_PERMISSIONS_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (savedInstanceState == null) {
            // The Activity is NOT being re-created so we can instantiate a new Fragment
            // and add it to the Activity
            HomeScreenFragment homeFragment = new HomeScreenFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.FragmentContainer, homeFragment, "HOME_FRAGMENT")
                    .commit();
        } else {
            // The Activity IS being re-created so we don't need to instantiate the Fragment or add it,
        }

    }

//Check permission to read location onResume so if user navigates away from app then back in still checks location permission
    protected void onResume(){
        super.onResume();
        getPermissionToReadLocation();
        createLocationManager();
    }

    public void getPermissionToReadLocation() {

        // Check for Fine Location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied it
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    READ_LOCATION_PERMISSIONS_REQUEST);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_LOCATION request
        if (requestCode ==  READ_LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static Location currentLocation;

    public static Location getLocation(){
        return currentLocation;
    }
    public void createLocationManager(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "New Latitude: " + location.getLatitude() + " New Longitude: " + location.getLongitude();
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        currentLocation = location;

        Intent locationChangedIntent = new Intent("HotOrColdLocationChanged");
        this.sendBroadcast(locationChangedIntent);

    }

    @Override
    public void onProviderDisabled(String provider) {
    // If GPS is OFF send User to Location Settings Page

        Snackbar snakbar = Snackbar.make(findViewById(R.id.myMainLayout), R.string.gps_off, Snackbar.LENGTH_INDEFINITE);
        snakbar.setAction(R.string.goToLocationSettings, new goToSettingsListener());
        snakbar.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Snackbar.make(findViewById(R.id.myMainLayout), R.string.gps_on, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public static GameManager getGameManager() {

        return gameManager;
    }


    public class goToSettingsListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }

}
