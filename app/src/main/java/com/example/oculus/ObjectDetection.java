package com.example.oculus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class ObjectDetection extends AppCompatActivity {

    private CameraView cameraView;                                                                  //for camera
    private Button detectBtn;                                                                       //button to detect
    private AlertDialog waitingDialogue;                                                            //to display waiting dialogue
    private TextToSpeech TTS;                                                                       //variable to implement text to speech
    private TextView display;                                                                       //to display
    private Vibrator vibrator;

    //for voice interaction status
    private float sensorSensitivity = 300;
    private boolean voiceCommands;

    //For sensors
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    //for speech recognition
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;


    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        cameraView.start();                                                                          //to start camera
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
        cameraView.stop();                                                                          //to stop camera (when the activity gets paused)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        detectBtn = findViewById(R.id.button_detect);
        display = findViewById(R.id.display_labels);
        cameraView = findViewById(R.id.cameraView);

        ///to check status of voice commands and enable/disable it
        Bundle bundle1 = getIntent().getExtras();
        voiceCommands = bundle1.getBoolean("Voice Command Status");
        if(voiceCommands){
            sensorSensitivity = 25;
        }

        //to set waiting dialog
        waitingDialogue = new SpotsDialog.Builder().setContext(this).setMessage("Please Wait")
                .setCancelable(false).build();

        //For Camera
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialogue.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth() , cameraView.getHeight() , false);
                cameraView.stop();                       //To stop camera after the image gets captured
                if(bitmap == null) {
                    display.setText("Try again...");
                }
                else{
                    runDetector(bitmap);
                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        //Text to speech
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = TTS.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.d("TTS","LANGUAGE NOT SUPPORTED");
                    }else {
                        Log.d("TTS","Done!");
                    }
                } else {
                    Log.d("TTS","Initialization Failed");
                }
            }
        });

        //To start detecting object in front of camera
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect();
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
                    if(string.toLowerCase().contains("detect Again")) { detect();}                 //add function and keyword
                    if(string.toLowerCase().contains("detect")) {detect();}                         //add function and keyword
                    if(string.toLowerCase().contains("back")) {onBackPressed();}
                }
                else{Toast.makeText(ObjectDetection.this, "Try Again!", Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


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


    private void detect() {
        vibrator.vibrate(100);
        display.setText(null);
        cameraView.start();
        cameraView.toggleFlash();
        cameraView.captureImage();
    }

    //function to give voice output

    private void voiceDetectOutput() {

        String text = display.getText().toString();
        TTS.setPitch(1);
        TTS.setSpeechRate(0.8f);
        TTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);

    }

    @Override
    protected void onDestroy() {
        if(TTS !=null){
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }

    private void runDetector(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);                         //To create a FirebaseVisionImage object from a Bitmap object


        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()                           //To get instance of FirebaseVisionImageLabeler
                .getOnDeviceImageLabeler();

        labeler.processImage(image)                                                                 //passing image to processImage() method
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {     //if it runs successfully the following method is called
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        try {
                            processDataResult(labels);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {          //if it fails following message is displayed
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        display.setText("No label found");
                    }
                });
    }

    private void processDataResult(List<FirebaseVisionImageLabel> labels) throws InterruptedException {         //to display labels

        if(labels != null) {                                    //if labels are present
            for (FirebaseVisionImageLabel label: labels) {
                float confidence = label.getConfidence();
                if (confidence >= 0.68){
                    String text = label.getText();
                    display.append(text + " " + "or" + " ");
                }
            }
            //to handle last "or"
            String string1 = display.getText().toString();
            String string2 = string1.substring(0,string1.length()-1);
            string1 = string2.substring(0,string2.length()-1);
            string2 = string1.substring(0,string1.length()-1);
            display.setText(string2);

        } else {display.setText("No Objects Detected");}

        if(waitingDialogue.isShowing())                                                             //to remove waiting dialogue
            waitingDialogue.dismiss();
         voiceDetectOutput();                                                //to give voice output of detected labels
    }

    /**To handle orientation changes
    * This saves the value displayed on the textView before a change in the orientation
    * */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Display Output",display.getText().toString());   //save textView output in our outstate bundle
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        display.setText(savedInstanceState.getString("Display Output"));   //receiving and displaying the value from our saved instance state bundle
    }

    @Override
    public void onBackPressed() {
        vibrator.vibrate(170);
        super.onBackPressed();
    }
}