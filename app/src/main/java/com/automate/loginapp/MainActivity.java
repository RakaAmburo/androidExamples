package com.automate.loginapp;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends Activity {

    Button switch_1;
    Button switch_2;
    Button switch_3;
    Button switch_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch_1 = findViewById(R.id.switch_1);
        switch_1.setText("On");
        switch_2 = findViewById(R.id.switch_2);
        switch_2.setText("On");
        switch_3 = findViewById(R.id.switch_3);
        switch_3.setText("On");
        switch_4 = findViewById(R.id.switch_4);
        switch_4.setText("On");


    }

    public void onToggleClick(View v)
    {

        if (v.getId() == R.id.switch_1){
            String response = sendBroadcast("SWITCH_1_OFF");
            Toast.makeText(getApplicationContext(),response ,Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.switch_2){
            String response = sendBroadcast("SWITCH_2_OFF");
            Toast.makeText(getApplicationContext(),response ,Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.switch_3){
            String response = sendBroadcast("SWITCH_3_OFF");
            Toast.makeText(getApplicationContext(),response ,Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.switch_4){
            String response = sendBroadcast("SWITCH_4_OFF");
            Toast.makeText(getApplicationContext(),response ,Toast.LENGTH_SHORT).show();
        }
    }

    private String sendBroadcast(String messageStr) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String statusMessage = "EMPTY";
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            InetAddress inetAddress = getBroadcastAddress();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, 8286);
            socket.send(sendPacket);
        } catch (IOException e) {
            statusMessage = "UDP broadcast error";
            Log.e("UDP broadcast error: ", "IOException: " + e.getMessage());
            return statusMessage;
        }

        byte[] receiveByte = new byte[1024];
        try (DatagramSocket receivingSocket = new DatagramSocket(8284)) {
            DatagramPacket receivePack = new DatagramPacket(receiveByte, receiveByte.length);
            receivingSocket.receive(receivePack);
            String receiveStr = new String(receiveByte, receivePack.getOffset(), receivePack.getLength());
            statusMessage = receiveStr;
        } catch (IOException e) {
            statusMessage = "UDP receive error";
            Log.e("UDP receive error: ", "IOException: " + e.getMessage());
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