package com.mad.team1.hotorcold;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;


public class InGameFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback{

    private boolean mDualPane;
    private Drawable background;
    private View myView;
    private Location currentLocation;
    private GoogleMap gameMap;

    @Override
    public void onMapReady(GoogleMap map) {
        gameMap = map;

        gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10));
        gameMap.setMyLocationEnabled(true);
        gameMap.getUiSettings().setScrollGesturesEnabled(false);
        gameMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                gameMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
    }

    protected void startBackgroundUpdate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Activity activity = getActivity();
                if(activity != null && isAdded() && currentLocation != null){
                    Bitmap bitmap = createHeatBitmap(getHeatHex());
                    background = new BitmapDrawable(getResources(), bitmap);
                }
                backgroundHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 300);
    }

    public Handler backgroundHandler = new Handler() {
        public void handleMessage(Message msg) {
            FrameLayout layout = (FrameLayout) myView.findViewById(R.id.in_game_container);
            layout.setBackground(background);

            if(MainActivity.getLocation() != null){
                currentLocation = MainActivity.getLocation();
                myView.findViewById(R.id.clipping_view).invalidate();
                if(gameMap != null && gameMap.getCameraPosition().zoom == 10){
                    gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                }
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;
        startBackgroundUpdate();
    }
    private static Bitmap createHeatBitmap(String color) {
        Paint p = new Paint();
        p.setDither(true);
        p.setColor(Color.parseColor(color));

        Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, 100, 200), p);

        return bitmap;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.in_game_screen, container, false);

        Button gamecomplete_Button = (Button) myView.findViewById(R.id.gamecomplete_button);
        gamecomplete_Button.setOnClickListener(this);

        MapFragment myMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.google_map_container, myMapFragment);
        fragmentTransaction.commit();
        myMapFragment.getMapAsync(this);

        return myView;
    }

    public String getHeatHex(){
        return MainActivity.getGameManager().getCurrentGame().calculateDistanceColour(currentLocation);
    }

    public void onClick(View v) {
        Fragment newFragment = null;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.gamecomplete_button:
                newFragment= new GameCompleteFragment();
                MainActivity.getGameManager().endCurrentGame(currentLocation);
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
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}

