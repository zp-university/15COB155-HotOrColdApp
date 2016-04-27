package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by GurinderSingh on 27/04/2016.
 */

public class HomeScreenFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.home_screen_layout, container, false);
        Button myButton = (Button) myView.findViewById(R.id.start_button);
        myButton.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.start_button:
                //switchFragment(HelpFragment.TAG);

                Fragment newFragment = new StartGameFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.FragmentContainer, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                break;
        }
    }


}