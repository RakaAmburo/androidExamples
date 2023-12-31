package com.automate.loginapp;


import android.widget.Button;

import java.util.Map;

public class ButtonHandler {

    private static final String MESSAGE_HEADER = "SWITCH_";
    private final int id;
    private final int messageId;
    private final int index;
    private final String name;
    private final Button button;
    private boolean status;
    private final UdpTransceiver udpTransceiver;
    private final Map<Integer, ButtonHandler> buttons;

    public ButtonHandler(int id, int index, String name, Button button, String strStatus,
                         UdpTransceiver udpTransceiver, Map<Integer, ButtonHandler> buttons) {
        this.id = id;
        this.index = index;
        this.messageId = index + 1;
        this.name = name;
        this.button = button;
        this.status = Integer.parseInt(strStatus) != 0;
        this.button.setText(getOnOffText());
        this.udpTransceiver = udpTransceiver;
        this.buttons = buttons;
    }

    public String toggle() {
        String message = buildMessage();
        message = udpTransceiver.sendBroadcast(message);
        String[] statuses = message.split(":");
        String response;
        if (statuses.length != this.buttons.size()) {
            response = "Problem switching " + name + "; Error: " + message;
        } else {
            status = !status;
            button.setText(getOnOffText());
            response = name + " is " + getOnOffText();
            /* check and correct the status off the rest */
            buttons.values().stream().filter(btnHlr -> btnHlr.id != this.id)
                    .forEach(btn -> {
                        boolean btnRetStatus = Integer.parseInt(statuses[btn.index]) != 0;
                        if (btn.status != btnRetStatus) {
                            btn.status = !btn.status;
                            btn.button.setText(btn.getOnOffText());
                        }
                    });
        }

        return response;
    }

    private String buildMessage() {
        return MESSAGE_HEADER + messageId + "_" + (status ? "OFF" : "ON");
    }

    private String getOnOffText() {
        return status ? "On" : "Off";
    }
}
