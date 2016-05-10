package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.mad.team1.hotorcold.api.Game;

public class InGameFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback{

    private boolean mDualPane;
    private Drawable background;
    private Drawable mapClipper;
    private View myView;
    private GoogleMap gameMap;
    private boolean mapLoading = false;
    private Location currentLocation;

    @Override
    public void onMapReady(GoogleMap map) {
        gameMap = map;
        gameMap.setMyLocationEnabled(true);
        gameMap.getUiSettings().setScrollGesturesEnabled(false);
        gameMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                gameMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                gameMap.getUiSettings().setZoomGesturesEnabled(false);
            }
        });

        gameMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location){
                currentLocation = location;
                Bitmap bgBitmap = createBgHeatBitmap(getHeatHex());
                Bitmap clipperBitmap = createClipperHeatBitmap(getHeatHex(), 600, 600);
                background = new BitmapDrawable(getResources(), bgBitmap);
                mapClipper = new BitmapDrawable(getResources(), clipperBitmap);

                myView.findViewById(R.id.in_game_container).setBackground(background);
                myView.findViewById(R.id.clipping_view).setBackground(mapClipper);

                if(gameMap.getCameraPosition().zoom == 15F){
                    gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                }
                else if(!mapLoading){
                    gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10));
                    mapLoading = true;
                }

                TextView distanceTravelled = (TextView)getActivity().findViewById(R.id.distance_travelled);
                //distanceTravelled.setText("Distance Travelled: "+ MainActivity.getGameManager());
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;
    }
    private static Bitmap createBgHeatBitmap(String color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.parseColor(color));

        Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, 100, 200), p);

        return bitmap;
    }
    private static Bitmap createClipperHeatBitmap(String color, int height, int width) {
        Bitmap windowFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(windowFrame);

        RectF outerRectangle = new RectF(0, 0, width, height);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.parseColor(color));
        canvas.drawRect(outerRectangle, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)); // A out B http://en.wikipedia.org/wiki/File:Alpha_compositing.svg
        float centerX = width / 2;
        float centerY = height / 2;
        float radius = Math.min(width, height) / 2 - 50;
        canvas.drawCircle(centerX, centerY, radius, p);

        return windowFrame;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.in_game_screen, container, false);

        Button gamecomplete_Button = (Button) myView.findViewById(R.id.gamecomplete_button);
        gamecomplete_Button.setOnClickListener(this);

        MapFragment myMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
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

