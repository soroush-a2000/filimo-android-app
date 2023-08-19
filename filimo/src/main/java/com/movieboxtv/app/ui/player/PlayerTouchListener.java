package com.movieboxtv.app.ui.player;

import android.view.MotionEvent;
import android.view.View;

public class PlayerTouchListener implements View.OnTouchListener {

    private float downX, downY;
    private boolean intLeft, intRight;
    private int sWidth, sHeight;
    private long diffX, diffY;
    boolean firstTouch = false;
    long time;
    int i;


    public PlayerTouchListener(int i) {
        this.i = i;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstTouch && (System.currentTimeMillis() - time) <= 300) {
                    firstTouch = false;
                    DoubleTap();
                } else {
                    firstTouch = true;
                    time = System.currentTimeMillis();
                    OneTap();
                }
                //touch is start
                downX = event.getX();
                downY = event.getY();
                if (event.getX() < (sWidth / 2)) {

                    //here check touch is screen left or right side
                    intLeft = true;
                    intRight = false;

                } else if (event.getX() > (sWidth / 2)) {

                    //here check touch is screen left or right side
                    intLeft = false;
                    intRight = true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:

                //finger move to screen
                float x2 = event.getX();
                float y2 = event.getY();

                diffX = (long) (Math.ceil(event.getX() - downX));
                diffY = (long) (Math.ceil(event.getY() - downY));


                if (Math.abs(diffY) > Math.abs(diffX)) {
                    if (intRight) {
                        if (downY < y2) {
                            if (go % i == 0)
                                Down();
                            go++;
                        } else if (downY > y2) {
                            if (go % i == 0)
                                Top();
                            go++;
                        }
                    }

                }
        }
        return true;
    }

    private int go = 0;

    public void Top() {

    }

    public void Down() {

    }

    public void DoubleTap() {
    }

    public void OneTap() {

    }
}