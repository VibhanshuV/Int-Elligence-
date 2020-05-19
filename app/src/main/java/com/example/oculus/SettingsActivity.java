package com.example.oculus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    //For sensors
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    //for speech recognition
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private boolean voiceCommands = false;//voice command status
    private float sensorSensitivity = 300;
    private Vibrator vibrator;
    private Button appInfoButton, talkBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //to make navigation bar transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Bundle bundle = getIntent().getExtras();
        voiceCommands = bundle.getBoolean("Voice Command Status");
        if(voiceCommands){
            sensorSensitivity = 25;
        }

        appInfoButton = findViewById(R.id.appInfoBtn);
        talkBackButton = findViewById(R.id.accessBtn);

        //to open app info and talk back feature

        appInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoAppInfo();

            }
        });

        talkBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoTalkBack();

            }
        });


        //For Sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        //Setting Up Speech Recognizer
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer = speechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string = null;
                if(matches!=null){
                    string = matches.get(0);
                    if(string.toLowerCase().contains("talkback")) {gotoTalkBack();}                 //add function and keyword
                    if(string.toLowerCase().contains("App Info")) {gotoAppInfo();}                  //add function and keyword
                    if(string.toLowerCase().contains("back") || string.toLowerCase().contains("exit")) {onBackPressed();}
                }
                else{Toast.makeText(SettingsActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


    }

    private void gotoAppInfo() {                    //opens application manager
        vibrator.vibrate(150);
        startActivityForResult(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS), 0);

    }

    private void gotoTalkBack() {                    //opens accessibility settings
        vibrator.vibrate(150);
        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > sensorSensitivity) {
                Toast.makeText(getApplicationContext(), "Say Something", Toast.LENGTH_SHORT).show();
                speechRecognizer.startListening(intentRecognizer);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {                      //finish this activity
        vibrator.vibrate(170);
        super.onBackPressed();
    }
}