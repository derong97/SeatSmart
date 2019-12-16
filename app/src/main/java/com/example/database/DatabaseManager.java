package com.example.database;

import android.util.Log;

import com.example.seatsmart.Facility;
import com.example.seatsmart.Seat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.example.utility.DataRef.mapSeat;
import static com.example.utility.DataRef.seatStatus;

public class DatabaseManager implements Subject{

    // TAG reference for logging
    private static final String TAG = "Admin";

    // The only instance of Database Manager
    private static DatabaseManager model;

    // Firebase reference
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();

    // Keep track of registered pages
    private ArrayList<Observer> observers = new ArrayList<>();

    // Keep track of which facility has been changed
    private Facility facilityChanged;

    // Singleton Design Pattern
    public static DatabaseManager getModel() {
        if (model == null) {
            model = new DatabaseManager();
        }
        return model;
    }

    @Override
    public void register(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for(Observer o: observers){
            o.update(facilityChanged);
        }
    }

    public void addValueEventListener(){
        for (String key : mapSeat.keySet()) {
            final String level = key.split("_")[0];
            String seat = key.split("_")[1];

            // adds event listener to every seat in every floor
            // updates seat status upon detecting change in value in Firebase
            myRef.child(level).child(seat).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = level + "_" + dataSnapshot.getKey();
                    String status = dataSnapshot.getValue().toString();
                    facilityChanged = new Seat(id, status);
                    seatStatus.put(id, (Seat) facilityChanged);
                    notifyObservers();
            }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, databaseError.getMessage());
                }
            });
        }
    }
}

