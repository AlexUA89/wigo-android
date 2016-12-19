package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.wigo.android.ui.MainActivity;

/**
 * Created by AlexUA89 on 12/14/2016.
 */

public class BootSplash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
