package com.mad.team1.hotorcold;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class InGameFragment extends Fragment implements View.OnClickListener{

    private boolean mDualPane;
    private Drawable background;
    private View myView;

    protected void startBackgroundUpdate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Activity activity = getActivity();
                if(activity != null && isAdded()){
                    Bitmap bitmap = createHeatBitmap(getHeatHex());
                    background = new BitmapDrawable(getResources(), bitmap);
                    backgroundHandler.obtainMessage(1).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    public Handler backgroundHandler = new Handler() {
        public void handleMessage(Message msg) {
            FrameLayout layout = (FrameLayout) myView.findViewById(R.id.in_game_container);
            layout.setBackground(background);
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

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    private static int count = 0;
    private static String direction = "up";
    private static String getHeatHex(){

        if(count == 0 ){
            count ++;
            direction = "up";
            return "#48b4fc";
        }
        else if(count == 1){
            if(direction.equals("up")) count ++;
            else count --;
            return "#7299d0";
        }
        else if(count == 2){
            if(direction.equals("up")) count ++;
            else count --;
            return "#a07ca0";
        }
        else if(count == 3){
            if(direction.equals("up")) count ++;
            else count --;
            return "#d16072";
        }
        else{
            direction = "down";
            count --;
            return "#ff4244";
        }
    }

    public void onClick(View v) {

        Fragment newFragment = null;
        // switch statement send to the correct fragment
        switch (v.getId()) {
            case R.id.gamecomplete_button:
                newFragment= new GameCompleteFragment();
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

        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}

