package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.team1.hotorcold.api.DistanceUnit;

/**
 * Created by GurinderSingh on 27/04/2016.
 */
public class StartGameFragment extends Fragment implements View.OnClickListener{

    private SeekBar seekBar;
    private TextView textView;
    private boolean mDualPane;
    private String unitTypePreference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.start_game_screen, container, false);

        Button startGame_Button = (Button) myView.findViewById(R.id.startGameButton);

        startGame_Button.setOnClickListener(this);
        //startGame_Button.setBackgroundResource(R.drawable.btn_disabled);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unitTypePreference = pref.getString("pref_units", "");
        boolean locationAvailable = false;

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //locationAvailable = true;
            }
        }, new IntentFilter("HotOrColdLocationChanged"));

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.start_game_screen, container, false);
        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;
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

    @Override
    public void onClick(View v) {
        Fragment newFragment = null;
        boolean validClick = true;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.startGameButton: {
                if(MainActivity.getLocation() != null){
                    newFragment= new InGameFragment();
                    DistanceUnit distanceUnit;
                    switch (unitTypePreference){
                        case "Metric": distanceUnit = DistanceUnit.KILOMETERS; break;
                        case "Imperial": distanceUnit = DistanceUnit.MILES; break;
                        default: distanceUnit = DistanceUnit.NAUTICAL_MILES; break;
                    }
                    MainActivity.getGameManager().startNewGame(MainActivity.getLocation(), seekBar.getProgress()/2, distanceUnit);
                } else{
                    validClick = false;

                    Snackbar.make(v, "Oops! Still searching for GPS signal", Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if(validClick){
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


        String units;
        switch (unitTypePreference){
            case "Metric": units = "Km"; break;
            case "Imperial": units = "Miles"; break;
            default: units = ""; break;
        }

        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref_units", Context.MODE_PRIVATE);
        textView.setText("Distance: " + distance + " " + units);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(seekBar != null){
            outState.putInt("seekBarProgress", seekBar.getProgress());
        }
    }

}
