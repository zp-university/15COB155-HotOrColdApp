package com.mad.team1.hotorcold;

import android.annotation.SuppressLint;
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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;


public class InGameFragment extends Fragment implements View.OnClickListener{

    boolean mDualPane;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Check to see if we have a sideContent in which to embed a fragment directly
        View sideContentFrame = getActivity().findViewById(R.id.sideContent);
        mDualPane = sideContentFrame != null && sideContentFrame.getVisibility() == View.VISIBLE;

    }
    private Bitmap createDynamicGradient(String color) {
        int colors[] = new int[3];
        colors[0] = Color.parseColor(color);
        colors[1] = Color.parseColor("#123456");
        colors[2] = Color.parseColor("#123456");

        LinearGradient gradient = new LinearGradient(0, 0, 0, 400, Color.RED, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        //p.setShader(gradient);
        p.setColor(Color.parseColor(color));

        Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, 100, 200), p);

        return bitmap;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.in_game_screen, container, false);

        Button gamecomplete_Button = (Button) myView.findViewById(R.id.gamecomplete_button);
        gamecomplete_Button.setOnClickListener(this);

        Bitmap bitmap = createDynamicGradient("#c10c10");

        // Scale it to 50 x 50
        //Drawable drawableScaled = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        Drawable drawableScaled = new BitmapDrawable(getResources(), bitmap);


        FrameLayout layout =(FrameLayout) myView.findViewById(R.id.in_game_container);
        layout.setBackground(drawableScaled);

        return myView;
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

