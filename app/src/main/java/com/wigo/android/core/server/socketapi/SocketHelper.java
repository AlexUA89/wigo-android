package com.wigo.android.core.server.socketapi;

import com.wigo.android.R;
import com.wigo.android.core.AppLog;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.Dto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.DeploymentException;

public class SocketHelper {

    private static final String TAG = SocketHelper.class.getCanonicalName();
    private static WebsocketClientEndpoint clientEndPoint;
    private static String socketServerUrl = "";
//    private static String socketServerUrl = ContextProvider.getAppContext().getResources().getString(R.string.socket_server_url);
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    //TODO should be hashmap of listeners
    private static MessageHandler listener;

    public static boolean checkConnection() {
        if (SharedPrefHelper.getToken(null) == null) {
            return false;
        }
        if (clientEndPoint == null) {
            try {
                clientEndPoint = new WebsocketClientEndpoint(new URI(socketServerUrl + "/?token=" + SharedPrefHelper.getToken("")), socketListener);
            } catch (URISyntaxException | DeploymentException | IOException e) {
                AppLog.E(TAG, e);
                return false;
            }
        }
        return true;
    }

    public synchronized static void closeConnection() {
        if (clientEndPoint != null) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    clientEndPoint.close();
                    clientEndPoint = null;
                    AppLog.D(TAG, "Socket connection closed from android side");
                }
            });
        } else {
            AppLog.D(TAG, "Socket connection already closed");
        }
    }

    public static void sendMessage(Dto message, MessageHandler msgH) {
        Long code = System.currentTimeMillis();
//        message.setCode(code);
        listener = msgH;
        try {
            final String msg = ContextProvider.getObjectMapper().writeValueAsString(message);
            final Future handler = executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (checkConnection()) {
                        clientEndPoint.sendMessage(msg);
                        AppLog.D(TAG, "Status have sent: " + msg);
                    } else {
                        AppLog.D(TAG, "Can not open connection");
                    }
                }
            });
            executor.schedule(new Runnable() {
                public void run() {
                    handler.cancel(true);
                }
            }, 10000, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static WebsocketClientEndpoint.SocketListener socketListener = new WebsocketClientEndpoint.SocketListener() {
        @Override
        public void handleMessage(String message) {
            //TODO should return DTO
            AppLog.D(TAG, "Received message");
            listener.receiveMessage(message);
        }

        @Override
        public void handleOnOpen() {
            AppLog.D(TAG, "Socket connection openned");
        }

        @Override
        public void handleOnClose(String message) {
            AppLog.D(TAG, "Socket connection closed");
            clientEndPoint = null;
        }
    };

    public static abstract class MessageHandler {
        public abstract void receiveMessage(String message);
    }


}
