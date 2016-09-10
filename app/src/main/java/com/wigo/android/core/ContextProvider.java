package com.wigo.android.core;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wigo.android.core.server.requestapi.WigoRestClient;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 30.08.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class ContextProvider extends Application {

    private static ContextProvider instance;

    private static ObjectMapper objectMapper;

    private static WigoRestClient wigoRestClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //facebook initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        wigoRestClient = new WigoRestClient();
    }

    synchronized public static ContextProvider getInstance() {
        return instance;
    }

    synchronized public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

    synchronized public static ObjectMapper getObjectMapper(){
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static void setInstance(ContextProvider instance) {
        ContextProvider.instance = instance;
    }

    public static WigoRestClient getWigoRestClient() {
        return wigoRestClient;
    }

    public static void setWigoRestClient(WigoRestClient wigoRestClient) {
        ContextProvider.wigoRestClient = wigoRestClient;
    }
}
