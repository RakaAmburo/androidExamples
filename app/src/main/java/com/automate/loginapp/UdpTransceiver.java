package com.automate.loginapp;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpTransceiver {

    public UdpTransceiver(WifiManager wifiManager, int sendingPort, int receivingPort) {
        this.wifiManager = wifiManager;
        this.sendingPort = sendingPort;
        this.receivingPort = receivingPort;
    }

    private final WifiManager wifiManager;
    private final int sendingPort;
    private final int receivingPort;

    public String sendBroadcast(String messageStr) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String statusMessage;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] sendData = messageStr.getBytes();
            InetAddress inetAddress = getBroadcastAddress();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, sendingPort);
            socket.send(sendPacket);
        } catch (IOException e) {
            statusMessage = "UDP broadcast error";
            Log.e("UDP broadcast error: ", "IOException: " + e.getMessage());
            return statusMessage;
        }

        byte[] receiveByte = new byte[1024];
        try (DatagramSocket receivingSocket = new DatagramSocket(receivingPort)) {
            DatagramPacket receivePack = new DatagramPacket(receiveByte, receiveByte.length);
            receivingSocket.receive(receivePack);
            statusMessage = new String(receiveByte, receivePack.getOffset(), receivePack.getLength());
        } catch (IOException e) {
            statusMessage = "UDP receive error";
            Log.e("UDP receive error: ", "IOException: " + e.getMessage());
        }

        return statusMessage;
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = this.wifiManager.getDhcpInfo();


        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
