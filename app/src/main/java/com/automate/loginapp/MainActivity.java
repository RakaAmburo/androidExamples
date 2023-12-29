package com.automate.loginapp;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class MainActivity extends Activity {

    PatternLockView mPatternLockView;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        password = "630487";
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                    Toast.makeText(MainActivity.this, "Password ok", Toast.LENGTH_SHORT).show();
                    String message = sendBroadcast("UNLOCK_MACHINE");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    mPatternLockView.clearPattern();
                }
            }

            @Override
            public void onCleared() {

            }
        });


    }

    private String sendBroadcast(String messageStr) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String statusMessage;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            InetAddress inetAddress = getBroadcastAddress();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, 8286);
            socket.send(sendPacket);
            statusMessage = "Broadcast sent to: " + inetAddress.getHostAddress();
        } catch (IOException e) {
            statusMessage = "UDP broadcast error";
            Log.e("UDP broadcast error: ", "IOException: " + e.getMessage());
        }

        return statusMessage;
    }

    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager)
                this.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();


        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

}