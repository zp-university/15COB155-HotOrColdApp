package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by GurinderSingh on 27/04/2016.
 */

public class HomeScreenFragment extends Fragment implements View.OnClickListener{

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
        // Inflate the layout for this fragment


        View myView = inflater.inflate(R.layout.home_screen_layout, container, false);

        Button start_Button = (Button) myView.findViewById(R.id.start_button);
        Button leaderboard_Button = (Button) myView.findViewById(R.id.leaderboards_button);
        Button instructions_Button = (Button) myView.findViewById(R.id.instruction_button);
        Button gamecomplete_Button = (Button) myView.findViewById(R.id.gamecomplete_button);

        start_Button.setOnClickListener(this);
        leaderboard_Button.setOnClickListener(this);
        instructions_Button.setOnClickListener(this);
        gamecomplete_Button.setOnClickListener(this);

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
            case R.id.instruction_button:
                newFragment= new InstructionsFragment();
                break;
            case R.id.gamecomplete_button:
                newFragment= new GameCompleteFragment();
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


}