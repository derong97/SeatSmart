package com.example.utility;

import android.media.MediaPlayer;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.seatsmart.Seat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DataRef {

    public static ArrayAdapter<String> adapter;
    public static MediaPlayer song;

    // Keep track of levels visited
    public static ArrayList<String> visited = new ArrayList();

    /* Example: {{"l1_s1", new Seat("l1_s1", "green")}, {"l1_s2", new Seat("l1_s2", "red"}} */
    public static HashMap<String, Seat> seatStatus = new HashMap<>();

    /* Example: {"l1_s1", (Context).findViewById(R.id.tvL1S1)}*/
    public static HashMap<String, TextView> mapSeat = new HashMap<>();

    // Keep track of the seats to be cleared
    public static ArrayList<String> seatClearList = new ArrayList<>();

    public static ArrayList<String> getSeatClearList() {
        Collections.sort(seatClearList);
        return seatClearList;
    }
}
