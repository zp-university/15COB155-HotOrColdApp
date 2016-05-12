package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.team1.hotorcold.data.MyScoreProvider;
import com.mad.team1.hotorcold.data.ScoreDataModel;

import org.w3c.dom.Comment;

/**
 * Created by GurinderSingh on 27/04/2016.
 */
public class LeaderboardsFragment extends Fragment{

    private ListView listView;
    private SimpleCursorAdapter adapter;
    private Cursor mCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.leaderboards_screen, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) getActivity().findViewById(R.id.leaderboard_listview);

        String[] mProjection = {
                ScoreDataModel.scoreEntry._ID,
                ScoreDataModel.scoreEntry.COLUMN_SCORE_VALUE
        };

        mCursor = getActivity().getContentResolver().query(ScoreDataModel.scoreEntry.CONTENT_URI,
                mProjection,
                null,
                null,
               "SCORE_VALUE DESC"
        );

        adapter = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.list_layout,
                mCursor,
                new String[] { ScoreDataModel.scoreEntry.COLUMN_SCORE_VALUE},
                new int[] { R.id.scoreTotal},
                0
        );

       listView.setAdapter(adapter);


    }


}
