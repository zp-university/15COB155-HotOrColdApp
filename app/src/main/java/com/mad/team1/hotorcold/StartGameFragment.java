package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by GurinderSingh on 27/04/2016.
 */
public class StartGameFragment extends Fragment implements View.OnClickListener, LocationListener{

    private LocationManager locationManager;

    private SeekBar seekBar;
    private TextView textView;
    boolean mDualPane;


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.start_game_screen, container, false);

        Button startGame_Button = (Button) myView.findViewById(R.id.startGameButton);

        startGame_Button.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 2000, 10, this);


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.start_game_screen, container, false);
        return myView;
    }


    @Override
    public void onClick(View v) {


        Fragment newFragment = null;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.startGameButton:
                newFragment= new InGameFragment();
                break;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (mDualPane) {
            // In dual-pane mode, show fragment in sideContent
            transaction.replace(R.id.sideContent, newFragment);

        }
        else{
            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.FragmentContainer, newFragment);
        }

        // and add the transaction to the back stack

        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        textView = (TextView) view.findViewById(R.id.seekBarText);

        if( savedInstanceState != null ) {
            showSeekbarDistance(savedInstanceState.getInt("seekBarProgress"));
        }
        else {
            showSeekbarDistance(seekBar.getProgress());
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                showSeekbarDistance(progressValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }
    public void showSeekbarDistance(int progress){
        progress = progress/2;
        int distance;
        if (progress < 1) {
            distance = 1;
            seekBar.setProgress(2);
        } else {
            distance = progress;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = pref.getString("pref_units", "");
        String units;
        switch (unitType){
            case "Metric": units = "Km"; break;
            case "Imperial": units = "Miles"; break;
            default: units = ""; break;
        }

        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref_units", Context.MODE_PRIVATE);
        textView.setText("Distance: " + distance +" "+ units);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seekBarProgress", seekBar.getProgress());
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getActivity().getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getActivity().getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
