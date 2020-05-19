package com.example.oculus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {

    float speed, pitch;
    SeekBar seekBar_speed, seekBar_pitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);

        seekBar_pitch = findViewById(R.id.seek_bar1);
        seekBar_speed = findViewById(R.id.seek_bar2);
    }


    public void setValueOfSpeedAndPitch(){
        speed = (float) seekBar_speed.getProgress()/50;
        if(speed<0.1)
            speed = 0.1f;

        pitch = (float) seekBar_pitch.getProgress()/50;
        if(pitch<0.1)
            pitch = 0.1f;
    }


}
