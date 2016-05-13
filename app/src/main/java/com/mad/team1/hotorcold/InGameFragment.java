package com.mad.team1.hotorcold;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mad.team1.hotorcold.data.ScoreDataModel;

import java.util.concurrent.TimeUnit;

public class InGameFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private boolean mDualPane;
    private Drawable background;
    private Drawable mapClipper;
    private View myView;
    private GoogleMap gameMap;
    private static  Location currentLocation;
    private String colorHash = "#44b6ff";
    private static boolean mapZoomed = false;
    private boolean mapMoved = false;
    private int clipperWidthHeight;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;
        if( savedInstanceState != null ) {
            currentLocation.setLatitude(savedInstanceState.getDouble("currentLat"));
            currentLocation.setLongitude(savedInstanceState.getDouble("currentLong"));
        }

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


    @Override
    public void onStart() {
        super.onStart();

        // build notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("HotOrCold")
                .setContentText("Game in Progress");

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }




    @Override
    public void onResume() {
        super.onResume();
        // register receiver on receiving battery change intent and battery low
        getActivity().registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // Used to overwrite the back button press on this fragment, you cannot go back to game after it is complete
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Overwriting pressing back key
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Snackbar.make(v, "Please end the game to get back to the menu", Snackbar.LENGTH_SHORT).show();
                    // handle back button's click listener
                    return true;
                }
                return false;
            }

        });


        final FrameLayout mapContainer = (FrameLayout) myView.findViewById(R.id.google_map_container);
        final FrameLayout clippingView = (FrameLayout) myView.findViewById(R.id.clipping_view);
        final ViewTreeObserver vto = mapContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                final int mapWidth = mapContainer.getWidth();
                final int mapHeight = mapContainer.getHeight();
                int mapWidthHeight;
                if (mapWidth < mapHeight) {
                    mapWidthHeight = mapWidth;
                } else {
                    mapWidthHeight = mapHeight;
                }
                System.out.println("width:"+mapWidth);
                System.out.println("height:"+mapHeight);
                System.out.println("implemented:"+mapWidthHeight);
                FrameLayout.LayoutParams mapLayoutParams = new FrameLayout.LayoutParams(mapWidthHeight, mapWidthHeight, 1);
                mapContainer.setLayoutParams(mapLayoutParams);

                final int clipperWidth = mapContainer.getWidth();
                final int clipperHeight = mapContainer.getHeight();
                if (clipperWidth < clipperHeight) {
                    clipperWidthHeight = clipperWidth;
                } else {
                    clipperWidthHeight = clipperHeight;
                }
                System.out.println("width:"+clipperWidth);
                System.out.println("height:"+clipperHeight);
                System.out.println("implemented:" + clipperWidthHeight);

                FrameLayout.LayoutParams clipperLayoutParams = new FrameLayout.LayoutParams(clipperWidthHeight, clipperWidthHeight, 1);
                clippingView.setLayoutParams(clipperLayoutParams);

                Bitmap clipperBitmap = createClipperHeatBitmap(colorHash, clipperWidthHeight, clipperWidthHeight);
                mapClipper = new BitmapDrawable(getResources(), clipperBitmap);
                myView.findViewById(R.id.clipping_view).setBackground(mapClipper);

                mapContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister mBatInfoReceiver
        getActivity().unregisterReceiver(mBatInfoReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        gameMap.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        destroyInGameNotification();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentLocation != null){
            outState.putDouble("currentLat", currentLocation.getLatitude());
            outState.putDouble("currentLong", currentLocation.getLongitude());
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        gameMap = map;
        gameMap.setMyLocationEnabled(true);
        gameMap.getUiSettings().setScrollGesturesEnabled(false);
        if (mapZoomed) {
            gameMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
        }
        gameMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //mapZoomed = true;
                if (!mapZoomed){
                    gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                    mapZoomed = true;
                }
                gameMap.getUiSettings().setZoomGesturesEnabled(false);
                mapMoved = true;
            }
        });
        gameMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                currentLocation = location;
                colorHash = MainActivity.getGameManager().getCurrentGame().calculateDistanceColour(location);
                Bitmap bgBitmap = createBgHeatBitmap(colorHash);

                background = new BitmapDrawable(getResources(), bgBitmap);
                myView.findViewById(R.id.in_game_container).setBackground(background);

                if (mapMoved) {
                    gameMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                }

                TextView distanceTravelled = (TextView) getActivity().findViewById(R.id.distance_travelled);
                distanceTravelled.setText("Distance Travelled: " + MainActivity.getGameManager().getCurrentGame().getTravelDistance() + "m");
                Long time = (System.currentTimeMillis() - MainActivity.getGameManager().getCurrentGame().getStartTime());

                String outputTime = String.format("%02d min, %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(time),
                        TimeUnit.MILLISECONDS.toSeconds(time) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
                );
                TextView timePlayed = (TextView) getActivity().findViewById(R.id.time_played);
                timePlayed.setText("Time Played: " + outputTime + "s");

                Bitmap clipperBitmap = createClipperHeatBitmap(colorHash, clipperWidthHeight, clipperWidthHeight);
                mapClipper = new BitmapDrawable(getResources(), clipperBitmap);
                myView.findViewById(R.id.clipping_view).setBackground(mapClipper);
            }
        });
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

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {
            //Get Battery %
            int level = i.getIntExtra("level", 0);
            TextView tv = (TextView) getActivity().findViewById(R.id.battery_percentage);
            //Set TextView with text
            if (tv != null) tv.setText("Battery Level: " + Integer.toString(level) + "%");
        }

    };


    public void saveGameData(){
        Integer gameScore = MainActivity.getGameManager().getCurrentGame().calculateScore();
        String gameScoreString = gameScore.toString();
        ContentValues values = new ContentValues();
        values.put(ScoreDataModel.scoreEntry.COLUMN_SCORE_TIME, System.currentTimeMillis());
        values.put(ScoreDataModel.scoreEntry.COLUMN_SCORE_VALUE, gameScoreString);
        Uri uri = getActivity().getContentResolver().insert(ScoreDataModel.scoreEntry.CONTENT_URI, values);
    }

    public void onClick(View v) {
        Fragment newFragment = null;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.gamecomplete_button:
                newFragment= new GameCompleteFragment();
                saveGameData();
                MainActivity.getGameManager().endCurrentGame(currentLocation);
                mapZoomed = false;
                destroyInGameNotification();
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

        // Commit the transaction
        transaction.commit();
    }

    public void destroyInGameNotification(){
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(001);
    }


}

