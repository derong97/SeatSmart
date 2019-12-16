package com.example.seatsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteTaking extends AppCompatActivity {

    // TAG reference for logging
    private static final String TAG = "Admin";

    // UI reference
    private Button btnBackToPrev;
    private EditText edtNoteContent;
    private Button btnSave;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.mainsharedprefs";
    private String storedString;

    // Variable reference
    public static final String NOTE_KEY = "NOTE_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);

        // Initialize UI
        btnBackToPrev = findViewById(R.id.btnBackToPrev);
        edtNoteContent = findViewById(R.id.edtNoteContent);
        btnSave = findViewById(R.id.btnSave);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        storedString = mPreferences.getString(NOTE_KEY, "");
        edtNoteContent.setText(storedString);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(NOTE_KEY, edtNoteContent.getText().toString());
                editor.apply();
                Toast.makeText(NoteTaking.this, "Your note has been saved!",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Go back to the previous activity
        btnBackToPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
