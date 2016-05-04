package com.mad.team1.hotorcold;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (savedInstanceState == null) {
            // The Activity is NOT being re-created so we can instantiate a new Fragment
            // and add it to the Activity
            HomeScreenFragment homeFragment = new HomeScreenFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.FragmentContainer, homeFragment, "HOME_FRAGMENT")
                    .commit();
        } else {
            // The Activity IS being re-created so we don't need to instantiate the Fragment or add it,
        }

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    /*public class MyBatteryLevelReceiver extends BroadcastReceiver

    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW))
            {
                Toast.makeText(context,R.string.low_battery_message, Toast.LENGTH_LONG).show();
                // do your task here.
            }
        }

    }*/

}
