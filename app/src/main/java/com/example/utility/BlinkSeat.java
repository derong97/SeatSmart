package com.example.utility;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class BlinkSeat {

    public static final String BLINKING_KEY = "BLINKING_KEY";

    public static void blink(final TextView textview) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 4) {
                    int timeToBlink = 1000;    //in milliseconds
                    try {
                        Thread.sleep(timeToBlink);
                    } catch (Exception e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (textview.getVisibility() == View.VISIBLE) {
                                textview.setVisibility(View.INVISIBLE);
                            } else {
                                textview.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    i++;
                }
            }
        }).start();
    }
}
