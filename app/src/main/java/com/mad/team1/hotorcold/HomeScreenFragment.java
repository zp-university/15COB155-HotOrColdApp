package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


/**
 * Created by GurinderSingh on 27/04/2016.
 */

public class HomeScreenFragment extends Fragment implements View.OnClickListener {

    Button myButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.home_screen_layout, container, false);
        myButton = (Button) myView.findViewById(R.id.start_button);
        myButton.setOnClickListener(this);
        return myView;
    }
    @Override
    public void onClick(View v) {
        // Create new fragment and transaction
        Fragment newFragment = new StartGameFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.home_screen_fragment, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }



}