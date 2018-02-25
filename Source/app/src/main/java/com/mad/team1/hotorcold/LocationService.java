package com.mad.team1.hotorcold;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

public class LocationService extends Service implements LocationListener {

    private final IBinder myBinder = new MyLocationBinder();
    private Location currentLocation;
    private LocationManager locationManager;
    private MainActivity mainActivity;

    @Override
    public IBinder onBind(Intent arg0) {
        this.mainActivity = MainActivity.getInstance();
        createLocationManager();
        return myBinder;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public class MyLocationBinder extends Binder {

        LocationService getService() {

            return LocationService.this;
        }
    }

    public void createLocationManager(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //String msg = "New Latitude: " + location.getLatitude() + " New Longitude: " + location.getLongitude();
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        currentLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // If GPS is OFF send User to Location Settings Page

        Snackbar snakbar = Snackbar.make(mainActivity.findViewById(R.id.myMainLayout), R.string.gps_off, Snackbar.LENGTH_INDEFINITE);
        snakbar.setAction(R.string.goToLocationSettings, new GoToSettingsListener());
        snakbar.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Snackbar.make(mainActivity.findViewById(R.id.myMainLayout), R.string.gps_on, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public class GoToSettingsListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mainActivity.startActivity(intent);
        }
    }
}
