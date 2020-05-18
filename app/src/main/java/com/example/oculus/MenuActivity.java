package com.example.oculus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class MenuActivity extends AppCompatActivity {

    private boolean voiceCommands = true;
    private Vibrator vibrator;              //for haptic feedback
    //For sensors
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    //for speech recognition
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ActivityCompat.requestPermissions(this,new String[] {RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        Button objectDetecBtn = findViewById(R.id.objectDetc);
        Button textReconBtn = findViewById(R.id.textRecon);
        Button voiceComBtn = findViewById(R.id.voiceCom);
        Button settingsBtn = findViewById(R.id.settings);
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
                vibrator.vibrate(200);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string = null;
                if(matches!=null){
                    string = matches.get(0);
                    if(string.toLowerCase().contains("text")) { openTextRecognizer(); }
                    if(string.toLowerCase().contains("object")) {openObjectDetection();}
                    if(string.toLowerCase().contains("settings")) {openSettings();}
                    if(string.toLowerCase().contains("close app")) {exit();}
                    if(string.toLowerCase().contains("voice commands")) {openSettings();}
                    else{vibrator.vibrate(200);}
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });




        objectDetecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               vibrator.vibrate(70);          //haptic feedback
                openObjectDetection();
            }

        });
        textReconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(70);
                openTextRecognizer();
            }

        });
        voiceComBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(150);
                toggleVoiceCommands();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(70);
                openSettings();
            }
        });

    }

    private void exit() {
        finish();
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
            if (mAccel > 20) {
                Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
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

    private void openObjectDetection(){
        Intent objDetector = new Intent(MenuActivity.this,ObjectDetection.class);
        //Intents to  ObjectDetection Activity
        startActivity(objDetector);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void openTextRecognizer(){
        Intent txtRecognition = new Intent(MenuActivity.this,OcrCaptureActivity.class);
        //Intents to Te OcrCaptureActivity
        startActivity(txtRecognition);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void toggleVoiceCommands(){
        voiceCommands = !voiceCommands;
    }

    private void openSettings(){
        Intent settingsMenu = new Intent(MenuActivity.this,SettingsActivity.class);
        //Intents to Settings Activity
        startActivity(settingsMenu);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

}
