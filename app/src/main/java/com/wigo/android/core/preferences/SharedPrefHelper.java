package com.wigo.android.core.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wigo.android.core.AppLog;
import com.wigo.android.core.ContextProvider;

import java.util.Calendar;
import java.util.Set;

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

    public static String getTagSearch(String defaulSearch) {
        return getSHP().getString(SPConstants.TAGS_SEARCH, defaulSearch);
    }

    public static void setTagSearch(String tagSearch) {
        getEditor().putString(SPConstants.TAGS_SEARCH, tagSearch).commit();
    }

    public static String getTextSearch(String defaulTextSearch) {
        return getSHP().getString(SPConstants.TEXT_SEARCH, defaulTextSearch);
    }

    public static void setTextSearch(String textSearch) {
        getEditor().putString(SPConstants.TEXT_SEARCH, textSearch).commit();
    }

    public static Set<String> getCategoriesSearch(Set<String> defaulCategoriesSearch) {
        return getSHP().getStringSet(SPConstants.CATEGORIES, defaulCategoriesSearch);
    }

    public static void setCategoriesSearch(Set<String> categoriesSearch) {
        getEditor().putStringSet(SPConstants.CATEGORIES, categoriesSearch).commit();
    }

    public static Calendar getFromDateSearch(Calendar defaulDateSearch) {
        long result = getSHP().getLong(SPConstants.FROM_DATE_SEARCH, -1);
        if (result != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(result);
            return calendar;
        }
        return defaulDateSearch;
    }

    public static void setFromDateSearch(Calendar dateSearch) {
        getEditor().putLong(SPConstants.FROM_DATE_SEARCH, dateSearch.getTimeInMillis()).commit();
    }

    public static Calendar getToDateSearch(Calendar defaulDateSearch) {
        long result = getSHP().getLong(SPConstants.TO_DATE_SEARCH, -1);
        if (result != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(result);
            return calendar;
        }
        return defaulDateSearch;
    }

    public static void setToDateSearch(Calendar dateSearch) {
        getEditor().putLong(SPConstants.TO_DATE_SEARCH, dateSearch.getTimeInMillis()).commit();
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
