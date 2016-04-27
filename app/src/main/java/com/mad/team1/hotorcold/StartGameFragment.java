package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by GurinderSingh on 27/04/2016.
 */
public class StartGameFragment extends Fragment {

    private SeekBar seekBar;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.start_game_screen, container, false);
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

}
