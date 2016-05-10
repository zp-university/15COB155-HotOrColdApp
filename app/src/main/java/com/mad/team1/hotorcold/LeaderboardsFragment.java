package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.w3c.dom.Comment;

/**
 * Created by GurinderSingh on 27/04/2016.
 */
public class LeaderboardsFragment extends Fragment {

    public ArrayAdapter<String> myArrayAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.leaderboards_screen, container, false);

        // Inflate the layout for this fragment8

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //myArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.list_item,R.id.item);
        //ListView lv = (ListView) getActivity().findViewById(R.id.leaderboard_listview);
        //lv.setAdapter(myArrayAdapter);


    }

}
