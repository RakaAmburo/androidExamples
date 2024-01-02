package com.automate.loginapp;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends Activity {

    Map<Integer, ButtonHandler> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new HashMap<>();
        /* This data would be configurable in a initial instance of the app configuration */
        int[] ids = {R.id.switch_1, R.id.switch_2, R.id.switch_3, R.id.switch_4};
        String[] names = {"Kitchen", "Living Room", "Garden", "Porch"};
        /* This data would be configurable in a initial instance of the app configuration */

        WifiManager wifiManager = (WifiManager)
                this.getSystemService(Context.WIFI_SERVICE);
        UdpTransceiver udpTransceiver = new UdpTransceiver(wifiManager, 8286,
                8284);
        String response = udpTransceiver.sendBroadcast("SWITCH_STATUS");

        String[] statuses = response.split(":");
        /* only when there is an error we show a message */
        if (statuses.length != ids.length) {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < statuses.length; i++) {
                Button button = findViewById(ids[i]);
                ButtonHandler bh = new ButtonHandler(ids[i], i, names[i], button,
                        statuses[i], udpTransceiver, buttons);
                buttons.put(ids[i], bh);
            }
        }
    }

    public void onToggleClick(View v) {
        String resp = Objects.requireNonNull(buttons.get(v.getId())).toggle();
        Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
    }

}