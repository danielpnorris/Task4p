package com.example.task4p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView lastActivity;
    Chronometer timer;
    EditText taskName;

    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton stopButton;

    String testSave = "testSave";
    boolean counting; //Variable to decide if timer should continue ticking
    long pausedTime; //The general method I used to obtain the paused time is based on https://stackoverflow.com/questions/5594877/android-chronometer-pause

    //SharedPreferences object as detailed in the lectures
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastActivity = findViewById(R.id.lastStudied);
        timer = findViewById(R.id.studyTimer);
        taskName = findViewById(R.id.taskName);
        playButton = findViewById(R.id.playbutton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        pausedTime = 0;

        sharedPref = getPreferences(0);
        editor = sharedPref.edit();

        counting = false;

        if(sharedPref.getString("lastActivity", "") != ""){
            lastActivity.setText(sharedPref.getString("lastActivity", ""));
        }

        if(savedInstanceState != null){
            timer.setBase(SystemClock.elapsedRealtime() + savedInstanceState.getLong("mostRecent"));
            if(savedInstanceState.getString("lastActivity") != "TextView"){
                lastActivity.setText(savedInstanceState.getString("lastActivity"));
            }
            if(savedInstanceState.getBoolean("isRunning") == true){
                timer.start();
                counting = true;
            }
            else{
                pausedTime = savedInstanceState.getLong("mostRecent");
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counting = true;
                timer.setBase(SystemClock.elapsedRealtime() + pausedTime);
                timer.start();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counting = false;
                pausedTime = timer.getBase() - SystemClock.elapsedRealtime();
                timer.stop();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counting = false;
                String lastValue = Long.toString(SystemClock.elapsedRealtime() + pausedTime);
                lastActivity.setText(String.format("You spent %1$s on %2$s last time", timer.getText(), taskName.getText()));
                editor.putString("lastActivity", String.valueOf(lastActivity.getText()));
                editor.commit();
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime() + 0);
                pausedTime = 0;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(pausedTime == 0L){
            pausedTime = timer.getBase() - SystemClock.elapsedRealtime();
        }
        outState.putLong("mostRecent", pausedTime);
        outState.putBoolean("isRunning", counting);
        outState.putString("lastActivity", String.valueOf(lastActivity.getText()));

        super.onSaveInstanceState(outState);
    }
}