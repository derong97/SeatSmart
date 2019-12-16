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
import static com.example.utility.BlinkSeat.blink;
import static com.example.utility.StringFormatting.reverseFormattedSeat;

public class Level2 extends AppCompatActivity implements Observer, View.OnClickListener {

    // TAG reference for logging
    private static final String TAG = "Admin";

    // UI reference
    private TextView tvL2S1;
    private TextView tvL2S2;
    private TextView tvL2S3;
    private LinearLayout llLevel2;
    private PopupWindow puLevel;
    private ListView listView;
    private ProgressDialog progressDialog;

    // Database reference
    private Subject database;

    private final String level = "l2";
    private String blinkingSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level2);

        // Ensures that every seat in each level is mapped for tracking when librarian first logs in
        if(!visited.contains(level)) {
            visited.add(level);
            Intent intent = new Intent(Level2.this, Level3.class);
            startActivity(intent);
        }

        // Initialize UI
        listView = findViewById(R.id.lvLevel2);
        tvL2S1 = findViewById(R.id.tvL2S1);
        tvL2S2 = findViewById(R.id.tvL2S2);
        tvL2S3 = findViewById(R.id.tvL2S3);
        llLevel2 = findViewById(R.id.llLevel2);
        song = MediaPlayer.create(Level2.this, R.raw.song);

        // Strategy Design Pattern
        adapter = new ArrayAdapter<>(Level2.this, R.layout.list_textview, getSeatClearList());
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

                    if(level.equals("Level1")){
                        Intent intent = new Intent(Level2.this, Level1.class);
                        intent.putExtra(BLINKING_KEY, blinkingSeat);
                        startActivity(intent);
                    }else if(level.equals("Level3")){
                        Intent intent = new Intent(Level2.this, Level3.class);
                        intent.putExtra(BLINKING_KEY, blinkingSeat);
                        startActivity(intent);
                    }else if(level.equals("Level2")) {
                        blink(mapSeat.get(blinkingSeat));
                    }
                }
            }
        });
    }

    private void levelPopUp(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.level_popup2,null);

        puLevel = new PopupWindow(
                customView,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            puLevel.setElevation(5.0f);
        }

        customView.findViewById(R.id.btnGoLevel1).setOnClickListener(this);
        customView.findViewById(R.id.btnGoLevel3).setOnClickListener(this);

        puLevel.showAtLocation(llLevel2, Gravity.CENTER,0,0);
    }

    private void screenInit(){
        // Mapping the key to the TextView widget
        mapSeat.put("l2_s1", tvL2S1);
        mapSeat.put("l2_s2", tvL2S2);
        mapSeat.put("l2_s3", tvL2S3);

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
            progressDialog = new ProgressDialog(Level2.this);
            progressDialog.setMessage("Logging out, please wait...");
            progressDialog.show();
            Log.d(TAG, "Logging out...");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Level2.this, LoginPage.class);
            startActivity(intent);
        } else if(i == R.id.btnNote){
            Log.d(TAG, "Creating Notes...");
            Intent intent = new Intent(Level2.this, NoteTaking.class);
            startActivity(intent);
        } else if(i == R.id.btnStatistics){
            Intent intent = new Intent(Level2.this, Statistics.class);
            startActivity(intent);
        } else if (i == R.id.btnMenuLevel){
            levelPopUp();
        } else if (i == R.id.btnGoLevel1){
            Intent intent = new Intent(Level2.this, Level1.class);
            startActivity(intent);
            puLevel.dismiss();
        } else if (i == R.id.btnGoLevel3){
            Intent intent = new Intent(Level2.this, Level3.class);
            startActivity(intent);
            puLevel.dismiss();
        }
    }
}
