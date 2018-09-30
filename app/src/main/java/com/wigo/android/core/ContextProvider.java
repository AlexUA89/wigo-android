package com.wigo.android.core;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.wigo.android.R;
import com.wigo.android.core.server.requestapi.WigoObjectMapper;
import com.wigo.android.core.server.requestapi.WigoRestClient;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.springframework.http.HttpMethod;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 30.08.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
@ReportsCrashes(formKey = "", // will not be used
        mailTo = "alexua89@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class ContextProvider extends Application {

    private static ContextProvider instance;

    private static WigoObjectMapper objectMapper;

    private static WigoRestClient wigoRestClient;

    private static HttpMethod GET = HttpMethod.valueOf("GET");

    @Override
    public void onCreate() {
        ACRA.init(this);
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

    synchronized public static WigoObjectMapper getObjectMapper() {
        if(objectMapper == null) {
            objectMapper = new WigoObjectMapper();
        }
        return objectMapper;
    }

    public static void setInstance(ContextProvider instance) {
        ContextProvider.instance = instance;
    }

    public static WigoRestClient getWigoRestClient() {
        if (wigoRestClient == null) {
            wigoRestClient = new WigoRestClient();
        }
        return wigoRestClient;
    }

    public static void setWigoRestClient(WigoRestClient wigoRestClient) {
        ContextProvider.wigoRestClient = wigoRestClient;
    }
}
