package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class GameCompleteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.game_complete_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyValueView scoreView = (KeyValueView) view.findViewById(R.id.score_view);
        scoreView.setText("Score: ", String.valueOf(MainActivity.getGameManager().getPreviousGame().calculateScore()));
    }
}
