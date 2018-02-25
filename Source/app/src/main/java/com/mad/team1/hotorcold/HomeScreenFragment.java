package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mad.team1.hotorcold.api.DistanceUnit;


/**
 * Created by GurinderSingh on 27/04/2016.
 */

public class HomeScreenFragment extends Fragment implements View.OnClickListener {

    boolean mDualPane;
    //private GestureDetectorCompat mDetector;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.home_screen_layout, container, false);

        Button start_Button = (Button) myView.findViewById(R.id.start_button);
        Button leaderboard_Button = (Button) myView.findViewById(R.id.leaderboards_button);

        start_Button.setOnClickListener(this);
        leaderboard_Button.setOnClickListener(this);

// Register GestureDetector
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    //Override onDown to return true so that we can then get register onDoubleTap
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        //On Double Tap Start New Game with Random Location within 10KM
                        Fragment newFragment = StartGameFragment.goToGame(myView, "Metric", 10);

                        if (MainActivity.getLocation() != null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            if (mDualPane) {
                                // In dual-pane mode, show fragment in sideContent
                                transaction.replace(R.id.sideContent, newFragment);
                            } else {
                                // Replace whatever is in the fragment_container view with this fragment,
                                transaction.replace(R.id.FragmentContainer, newFragment);
                            }
                            // and add the transaction to the back stack
                            transaction.addToBackStack(null);
                            // Commit the transaction
                            transaction.commit();
                        }
                        return true;
                    }
                });

        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        return myView;
    }

    @Override
    public void onClick(View v) {

        Fragment newFragment = null;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.start_button:
                newFragment = new StartGameFragment();
                break;
            case R.id.leaderboards_button:
                newFragment = new LeaderboardsFragment();
                break;
        }
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();

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