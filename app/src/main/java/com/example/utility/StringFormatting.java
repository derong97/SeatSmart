package com.example.utility;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import static android.graphics.Typeface.BOLD;

public class StringFormatting {

    /* Example: convert "l1s1" to "Level1 Seat1"*/
    public static String getFormattedSeat(String s){
        s = s.replace("l","Level")
                .replace("_", " ")
                .replace("s", "Seat");
        return s;
    }

    /* Example: convert "Level1 Seat1" to "l1s1"*/
    public static String reverseFormattedSeat(String s){
        s = s.replace("Level", "l")
                .replace(" ","_")
                .replace("Seat", "s");
        return s;
    }

    public static SpannableString setTextForYellow(Integer timeRemaining, Integer intSeatNo){
        String time = "Time: " + timeRemaining;
        SpannableString text = new SpannableString(time + "\n" + intSeatNo);
        text.setSpan(new RelativeSizeSpan(0.6f), 0, time.length(), 0);
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, time.length(), 0);
        text.setSpan(new StyleSpan(BOLD), 0, time.length(), 0);
        return text;
    }

    public static SpannableString setTextForGreenOrRed(Integer intSeatNo){
        return new SpannableString("\n" + intSeatNo);
    }
}
