package com.mad.team1.hotorcold;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mad.team1.hotorcold.api.GameManager;


public class MainActivity extends AppCompatActivity {

    private static final GameManager gameManager = GameManager.createGameManager();
    private static final int  READ_LOCATION_PERMISSIONS_REQUEST = 1;
    private static Vibrator vibrator;
    private ShareActionProvider mShareActionProvider;

    public static Vibrator getVibrator() {
        return vibrator;
    }

    private LocationService locationService;
    private boolean locationServiceBound;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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

        if(vibrator == null) vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

//Check permission to read location onResume so if user navigates away from app then back in still checks location permission
    protected void onResume(){
        super.onResume();
        getPermissionToReadLocation();

        registerReceiver(batteryLowReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryLowReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
        instance = null;
        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }


    private BroadcastReceiver batteryLowReceiver = new BroadcastReceiver() {
        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {
            // Show Low Battery Snackbar
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout), R.string.low_battery_message, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

        }

    };
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
// Gets current location from getLocationService
    public static Location getLocation(){
        return instance.getLocationService().getCurrentLocation();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    //Get GameManager
    public static GameManager getGameManager() {
        return gameManager;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    // Create Service Connection
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.MyLocationBinder myBinder = (LocationService.MyLocationBinder) service;
            locationService = myBinder.getService();
            locationServiceBound = true;
        }
    };

    public LocationService getLocationService() {
        return locationService;
    }


    //Create Share Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareIntent());
        return true;
    }

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.recommend_message));

        return shareIntent;
    }

    // Sets new share Intent
    private void changeShareIntent(Intent shareIntent) {
        mShareActionProvider.setShareIntent(shareIntent);
    }

}