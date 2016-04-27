package com.mad.team1.hotorcold;

import android.app.FragmentManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeScreenFragment homeFragment = new HomeScreenFragment();
        fragmentTransaction.add(R.id.FragmentContainer, homeFragment);
        fragmentTransaction.commit();

    }

}
