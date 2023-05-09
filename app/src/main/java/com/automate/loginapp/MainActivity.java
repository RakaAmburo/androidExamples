package com.automate.loginapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecognitionListener {

    private TextView capturedVoiceCmd;
    private Button activateSpeechRecognitionButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capturedVoiceCmd = findViewById(R.id.resultMessage);
        activateSpeechRecognitionButton = findViewById(R.id.mantener);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        setTitle("Voice control 1.0");
        activateSpeechRecognitionButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    askForPermission();
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    createSpeech();
                    speech.startListening(recognizerIntent);
                    break;
                case MotionEvent.ACTION_UP:
                    capturedVoiceCmd.setText("");
                    break;
            }
            return false;
        });

    }

    private void createSpeech() {
        if (speech != null) {
            speech.destroy();
        }
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    private void askForPermission() {
        String permission = "android.permission.RECORD_AUDIO";
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission}, 1);
            }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        progressBar.setIndeterminate(true);
        //worked
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        //speech.stopListening();
    }

    @Override
    public void onError(int errorCode) {
        if (errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            createSpeech();
            speech.startListening(recognizerIntent);
            return;
        }
        if (speech != null) {
            speech.destroy();
        }
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        String errorMessage = getErrorText(errorCode);
        capturedVoiceCmd.setText(errorMessage);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client retry";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private long lastClickTime = 0;

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        if (matches.size() == 0) {
            capturedVoiceCmd.setText("Listening Service Error");
        } else {
            capturedVoiceCmd.setText(matches.get(0));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (speech == null) {
            createSpeech();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
        }
    }
}