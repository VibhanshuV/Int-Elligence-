package com.example.oculus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import static android.Manifest.permission.RECORD_AUDIO;

public class MenuActivity extends AppCompatActivity {

    private boolean voiceCommands;
    private Vibrator vibrator;              //for haptic feedback
    private Button voiceComBtn;
    //For sensors
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private float sensorSensitivity;

    //for speech recognition
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String VOICE_STATUS = "VOICE_STATUS";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //to make navigation bar transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        ActivityCompat.requestPermissions(this,new String[] {RECORD_AUDIO, Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

        loadVoiceInteractionState();    //loading voice interaction status

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        Button objectDetecBtn = findViewById(R.id.objectDetc);
        Button textReconBtn = findViewById(R.id.textRecon);
        voiceComBtn = findViewById(R.id.voiceCom);
        Button settingsBtn = findViewById(R.id.settings);
         //For Sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        //sensor enabled/disabled according to voiceCommand status
        if(voiceCommands) {sensorSensitivity = 25;}
        else{sensorSensitivity = 300;
            voiceComBtn.setBackgroundResource(R.drawable.voicedeactivated);           //changes button background
            voiceComBtn.setContentDescription("Enable Voice Commands");}


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
                    if(string.toLowerCase().contains("close app") || string.toLowerCase().contains("quit")) {exit();}
                    if(string.toLowerCase().contains(" disable voice")) {
                        mSensorManager.unregisterListener(mSensorListener);
                        voiceComBtn.setBackgroundResource(R.drawable.voicedeactivated);           //changes button background
                        voiceComBtn.setContentDescription("Enable Voice Commands");
                    }
                }
                else{Toast.makeText(MenuActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();}
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
                openObjectDetection();
            }

        });
        textReconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTextRecognizer();
            }

        });
        voiceComBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVoiceCommands();
                if(voiceCommands == false) {                      //if voice command in deactivated
                    mSensorManager.unregisterListener(mSensorListener);                    //unregisters sensor manager
                    Toast.makeText(MenuActivity.this,"Voice Commands Disabled",Toast.LENGTH_SHORT).show();
                    voiceComBtn.setBackgroundResource(R.drawable.voicedeactivated);           //changes button background
                    voiceComBtn.setContentDescription("Enable Voice Commands");             //changes button description
                }
                if(voiceCommands == true) {                     //if voice commmand is activated
                    Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);  //register sensor manager
                    Toast.makeText(MenuActivity.this,"Voice Commands Enabled",Toast.LENGTH_SHORT).show();
                    voiceComBtn.setBackgroundResource(R.drawable.voice_button);
                    voiceComBtn.setContentDescription("Disable Voice Commands");
                }
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

    }

    private void exit() {
        finish();
    }


    private final SensorEventListener mSensorListener = new SensorEventListener() {                       //sensor reads values
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > sensorSensitivity) {                            //if maccel is greater than sensorSensitvity
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

    private void openObjectDetection(){
        vibrator.vibrate(100);          //haptic feedback
        Intent objDetector = new Intent(MenuActivity.this,ObjectDetection.class);
        objDetector.putExtra("Voice Command Status",voiceCommands);                             //sending the status of voice command(enabled/disabled) to objDetector activity
        //Intents to  ObjectDetection Activity
        startActivity(objDetector);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void openTextRecognizer(){
        vibrator.vibrate(100);          //haptic feedback
        Intent txtRecognition = new Intent(MenuActivity.this,OcrCaptureActivity.class);
        txtRecognition.putExtra("Voice Command Status",voiceCommands);                           //sending the status of voice command(enabled/disabled) to textRecognizer activity
        //Intents to Te OcrCaptureActivity
        startActivity(txtRecognition);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void toggleVoiceCommands(){
        vibrator.vibrate(500);
        voiceCommands = !voiceCommands;
        if(!voiceCommands){sensorSensitivity = 300;
            voiceComBtn.setBackgroundResource(R.drawable.voicedeactivated);           //changes button background
            voiceComBtn.setContentDescription("Enable Voice Commands");}
        else {
            sensorSensitivity = 25;
            voiceComBtn.setBackgroundResource(R.drawable.voice_button);
            voiceComBtn.setContentDescription("Disable Voice Commands");
        }

        saveVoiceInteractionState();
    }

    private void openSettings(){
        vibrator.vibrate(100);                                                              //haptic feedback
        Intent settingsMenu = new Intent(MenuActivity.this,SettingsActivity.class);     //sending the status of voice command(enabled/disabled) to Settings activity
        settingsMenu.putExtra("Voice Command Status",voiceCommands);
        //Intents to Settings Activity
        startActivity(settingsMenu);
    }


      //to handle orientation change
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Voice Command Status",voiceCommands);   //save textView output in our outstate bundle
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        voiceCommands = savedInstanceState.getBoolean(savedInstanceState.getString("Voice Command Status"));   //receiving and displaying the value from our saved instance state bundle
        if(voiceCommands) {sensorSensitivity = 25;}
        else{sensorSensitivity = 300;}

    }

    @Override
    public void onBackPressed() {                         //when back button is pressed (from navigation bar)
        vibrator.vibrate(120);
        saveVoiceInteractionState();
        super.onBackPressed();
    }

    //to save and load voice interaction status.

    public void saveVoiceInteractionState() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putBoolean(VOICE_STATUS,voiceCommands);
         editor.apply();
    }

    public void loadVoiceInteractionState() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        voiceCommands = sharedPreferences.getBoolean(VOICE_STATUS,true);
    }

}
