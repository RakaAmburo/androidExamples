package com.automate.loginapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.ebanx.swipebtn.SwipeButton;

public class MainActivity extends Activity {

    int s = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeButton swipeButton = findViewById(R.id.swipe);

        swipeButton.setOnStateChangeListener(active -> {
            s = s + 1;
            if (s % 2 == 0) {
                Toast.makeText(MainActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finish();
            }
        });
    }


}