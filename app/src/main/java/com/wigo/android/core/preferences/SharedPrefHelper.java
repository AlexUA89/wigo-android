package com.wigo.android.core.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wigo.android.core.AppLog;
import com.wigo.android.core.ContextProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Алексей
 * Date: 24.10.13
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class SharedPrefHelper {

    public static final String TAG = SharedPrefHelper.class.getCanonicalName();

    // -------------------------------------------------------BASIC FUNCTIONS------------------------------------------------------------
    private static SharedPreferences getSHP() {
        AppLog.D(TAG, "getSHP()");
        return PreferenceManager.getDefaultSharedPreferences(ContextProvider.getAppContext());
    }

    private static Editor getEditor() {
        AppLog.D(TAG, "getEditor()");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContextProvider.getAppContext());
        return sharedPreferences.edit();
    }

    // -------------------------------------------------------PUBLIC METHODS------------------------------------------------------------
    public static boolean getServiceState() {
        return getSHP().getBoolean(SPConstants.SERVISE_STATE, false);
    }

    public static void setServiceState(boolean state) {
        getEditor().putBoolean(SPConstants.SERVISE_STATE, state).commit();
    }


    // -------------------------------------------------------USER METHODS------------------------------------------------------------

    public static void setUserName(String name) {
        getEditor().putString(SPConstants.NAME, name).commit();
    }

    public static String getUserName(String defaultName) {
        return getSHP().getString(SPConstants.NAME, defaultName);
    }

    public static void setUserNickName(String nickName) {
        getEditor().putString(SPConstants.NICK_NAME, nickName).commit();
    }

    public static String getUserNickName(String defaultNickName) {
        return getSHP().getString(SPConstants.NICK_NAME, defaultNickName);
    }

    public static void setFacebookTooken(String token) {
        getEditor().putString(SPConstants.FACEBOOK_TOKEN, token).commit();
    }

    public static String getFacebookTooken(String defaultToken) {
        return getSHP().getString(SPConstants.EMAIL, defaultToken);
    }

    public static void setToken(String token) {
        getEditor().putString(SPConstants.SESSION_TOKEN, token).commit();

    }

    public static String getToken(String defaultToken) {
        return getSHP().getString(SPConstants.SESSION_TOKEN, defaultToken);
    }

    public static void setUserId(String userId) {
        getEditor().putString(SPConstants.USER_ID, userId).commit();
    }

    public static String getUserId(String defaultUserId) {
        return getSHP().getString(SPConstants.USER_ID, defaultUserId);
    }

    public static void setXLocal(Double coord) {
        if (coord != null) {
            getEditor().putString(SPConstants.X_LOCAL, coord.toString());
        }
    }

    public static Double getXLocal(Double defaultCoord) {
        String strCoord = getSHP().getString(SPConstants.X_LOCAL, null);
        if (TextUtils.isEmpty(strCoord)) {
            return defaultCoord;
        }
        return Double.parseDouble(strCoord);
    }

    public static void setYLocal(Double coord) {
        if (coord != null) {
            getEditor().putString(SPConstants.Y_LOCAL, coord.toString());
        }
    }

    public static Double getYLocal(Double defaultCoord) {
        String strCoord = getSHP().getString(SPConstants.Y_LOCAL, null);
        if (TextUtils.isEmpty(strCoord)) {
            return defaultCoord;
        }
        return Double.parseDouble(strCoord);
    }

    public static void setXGlobal(Double coord) {
        if (coord != null) {
            getEditor().putString(SPConstants.X_GLOBAL, coord.toString());
        }
    }

    public static Double getXGlobal(Double defaultCoord) {
        String strCoord = getSHP().getString(SPConstants.X_GLOBAL, null);
        if (TextUtils.isEmpty(strCoord)) {
            return defaultCoord;
        }
        return Double.parseDouble(strCoord);
    }

    public static void setYGlobal(Double coord) {
        if (coord != null) {
            getEditor().putString(SPConstants.Y_GLOBAL, coord.toString());
        }
    }

    public static Double getYGlobal(Double defaultCoord) {
        String strCoord = getSHP().getString(SPConstants.Y_GLOBAL, null);
        if (TextUtils.isEmpty(strCoord)) {
            return defaultCoord;
        }
        return Double.parseDouble(strCoord);
    }

}
