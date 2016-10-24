package com.wigo.android.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wigo.android.core.ContextProvider;

/**
 * Created by AlexUA89 on 10/24/2016.
 */

public class BitmapUtils {

    public static Bitmap getScaledBitmap(int recourceId, float scall_zero_to_one_f) {

        Bitmap bitmapIn = BitmapFactory.decodeResource(ContextProvider.getAppContext().getResources(), recourceId);
        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }
}
