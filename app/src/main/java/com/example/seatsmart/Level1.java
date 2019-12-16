package com.example.seatsmart;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.database.Observer;
import com.example.database.Subject;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.database.DatabaseManager.getModel;
import static com.example.utility.BlinkSeat.BLINKING_KEY;
import static com.example.utility.DataRef.adapter;
import static com.example.utility.DataRef.song;
import static com.example.utility.DataRef.getSeatClearList;
import static com.example.utility.DataRef.mapSeat;
import static com.example.utility.DataRef.seatStatus;
import static com.example.utility.DataRef.visited;
import static com.example.utility.StringFormatting.reverseFormattedSeat;
import static com.example.utility.BlinkSeat.blink;

public class Level1 extends AppCompatActivity implements Observer, View.OnClickListener{

    // TAG reference for logging
    private static final String TAG = "Admin";

    // UI reference
    private TextView tvL1S1;
    private TextView tvL1S2;
    private TextView tvL1S3;
    private LinearLayout llLevel1;
    private PopupWindow puLevel;
    private ListView listView;
    private ProgressDialog progressDialog;

    // Database reference
    private Subject database;

    private final String level = "l1";
    private String blinkingSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1);

        // Ensures that every seat in each level is mapped for tracking when librarian first logs in
        if(!visited.contains(level)) {
            visited.add(level);
            Intent intent = new Intent(Level1.this, Level2.class);
            startActivity(intent);
        }

        // Initialize UI
        listView = findViewById(R.id.listView);
        tvL1S1 = findViewById(R.id.tvL1S1);
        tvL1S2 = findViewById(R.id.tvL1S2);
        tvL1S3 = findViewById(R.id.tvL1S3);
        llLevel1 = findViewById(R.id.llLevel1);
        song = MediaPlayer.create(Level1.this, R.raw.song);

        // Strategy Design Pattern
        adapter = new ArrayAdapter<>(Level1.this, R.layout.list_textview, getSeatClearList());
        listView.setAdapter(adapter);
        View headerView = getLayoutInflater().inflate(R.layout.listview_header,null);
        listView.addHeaderView(headerView);

        // Initialize screen when first started
        screenInit();

        // Binding events on-click
        findViewById(R.id.btnLogout).setOnClickListener(this);
        findViewById(R.id.btnNote).setOnClickListener(this);
        findViewById(R.id.btnStatistics).setOnClickListener(this);
        findViewById(R.id.btnMenuLevel).setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){ // table header should not be pressed
                    String value = (String)listView.getItemAtPosition(i);
                    String level = value.split(" ")[0];
                    blinkingSeat = reverseFormattedSeat(value);

                    if(level.equals("Level2")){
                        Intent intent = new Intent(Level1.this, Level2.class);
                        intent.putExtra(BLINKING_KEY, blinkingSeat);
                        startActivity(intent);
                    }else if(level.equals("Level3")){
                        Intent intent = new Intent(Level1.this, Level3.class);
                        intent.putExtra(BLINKING_KEY, blinkingSeat);
                        startActivity(intent);
                    }else if(level.equals("Level1")) {
                        blink(mapSeat.get(blinkingSeat));
                    }
                }
            }
        });
    }

    private void levelPopUp(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.level_popup1,null);

        puLevel = new PopupWindow(
                customView,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            puLevel.setElevation(5.0f);
        }

        customView.findViewById(R.id.btnGoLevel2).setOnClickListener(this);
        customView.findViewById(R.id.btnGoLevel3).setOnClickListener(this);

        puLevel.showAtLocation(llLevel1, Gravity.CENTER,0,0);
    }

    private void screenInit(){
        // Mapping the key to the TextView widget
        mapSeat.put("l1_s1", tvL1S1);
        mapSeat.put("l1_s2", tvL1S2);
        mapSeat.put("l1_s3", tvL1S3);

        for(String key: seatStatus.keySet()){
            String getLevel = key.split("_")[0];
            if (getLevel.equals(level)){
                Seat seat = seatStatus.get(key);
                seat.update();
            }
        }
    }

    @Override
    public void update(Facility seat) {
        seat.update();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.database = getModel();
        this.database.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.database.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get intent from previous activity and blink if required
        Intent getIntent = getIntent();
        blinkingSeat = getIntent.getStringExtra(BLINKING_KEY);
        if (blinkingSeat != null){
            blink(mapSeat.get(blinkingSeat));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnLogout) {
            progressDialog = new ProgressDialog(Level1.this);
            progressDialog.setMessage("Logging out, please wait...");
            progressDialog.show();
            Log.d(TAG, "Logging out...");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Level1.this, LoginPage.class);
            startActivity(intent);
        } else if(i == R.id.btnNote){
            Log.d(TAG, "Creating Notes...");
            Intent intent = new Intent(Level1.this, NoteTaking.class);
            startActivity(intent);
        } else if(i == R.id.btnStatistics){
            Intent intent = new Intent(Level1.this, Statistics.class);
            startActivity(intent);
        } else if (i == R.id.btnMenuLevel){
            levelPopUp();
        } else if (i == R.id.btnGoLevel2){
            Intent intent = new Intent(Level1.this, Level2.class);
            startActivity(intent);
            puLevel.dismiss();
        } else if (i == R.id.btnGoLevel3){
            Intent intent = new Intent(Level1.this, Level3.class);
            startActivity(intent);
            puLevel.dismiss();
        }
    }
}
