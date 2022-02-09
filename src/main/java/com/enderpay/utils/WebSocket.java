package com.enderpay.utils;

import com.enderpay.Enderpay;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocket extends WebSocketClient {

    public static final String ENDPOINT = "wss://ws.enderpay.com";

    public WebSocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

        int serverId = Integer.parseInt(message);

        if (serverId == Enderpay.getServerId()) {
            Enderpay.checkForNewCommands();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
