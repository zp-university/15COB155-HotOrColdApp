package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class GameCompleteFragment extends Fragment implements View.OnClickListener{

    private boolean mDualPane;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.game_complete_screen, container, false);

        Button leaderboard_Button = (Button) myView.findViewById(R.id.leaderboards_button);
        Button home_Button = (Button) myView.findViewById(R.id.go_home_button);

        leaderboard_Button.setOnClickListener(this);
        home_Button.setOnClickListener(this);

        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;

        Button home_Button = (Button) getActivity().findViewById(R.id.go_home_button);

        if (mDualPane){
            home_Button.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyValueView scoreView = (KeyValueView) view.findViewById(R.id.score_view);
        scoreView.setText("Score: ", String.valueOf(MainActivity.getGameManager().getPreviousGame().calculateScore()));
    }

    @Override
    public void onResume() {
        super.onResume();


// Used to overwrite the back button press on this fragment, you cannot go back to game after it is complete
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    removeGameFromBackStack();
                    goHomeFragment();
                    // handle back button's click listener
                    Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }


        });

    }

    //Goes Home
    public void goHomeFragment() {
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        Fragment homeFragment = new HomeScreenFragment();

        transaction.replace(R.id.FragmentContainer, homeFragment);
        // and add the transaction to the back stack
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    //Removes InGameScreen from Fragment
    public  void removeGameFromBackStack(){
        String gameFragment = new String("InGameFragment");
        FragmentManager fm = getFragmentManager();
        fm.popBackStack(gameFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onClick(View v){
        Fragment newFragment = null;
        // switch statement send to the correct fragment

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mDualPane) {
            switch (v.getId()) {
                case R.id.leaderboards_button:
                    newFragment= new LeaderboardsFragment();
                    transaction.replace(R.id.sideContent, newFragment);
                    break;
                case R.id.go_home_button:
                    transaction.detach(this);
                    break;
            }
            // In dual-pane mode, show fragment in sideContent
        }
        else{
            switch (v.getId()) {
                case R.id.leaderboards_button:
                    newFragment= new LeaderboardsFragment();
                    break;
                case R.id.go_home_button:
                    newFragment= new HomeScreenFragment();
                    break;
            }
            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.FragmentContainer, newFragment);
        }

        // and add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


}
