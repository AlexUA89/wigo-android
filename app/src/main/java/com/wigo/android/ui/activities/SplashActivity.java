package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.wigo.android.core.ContextProvider;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by AlexUA89 on 12/14/2016.
 */

public class SplashActivity extends Activity {

    private static SplashActivity splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash = this;
    }

    @Override
    public void onBackPressed() {
    }

    public static void openSplashActivity() {
        Intent intent = new Intent(ContextProvider.getAppContext(), SplashActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ContextProvider.getAppContext().startActivity(intent);
    }

    public static void closeSplashActivity() {
        if (splash != null) {
            splash.finish();
            splash.overridePendingTransition(0, 0);
            splash = null;
        }
    }

}
