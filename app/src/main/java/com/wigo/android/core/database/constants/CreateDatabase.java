package com.wigo.android.core.database.constants;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 29.08.13
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public final class CreateDatabase {

    public static void createDatabase(SQLiteDatabase db) {
        db.execSQL(createStatusTableScript);
        db.execSQL(createMessageTableScript);
    }


    public static final String createStatusTableScript = "CREATE TABLE IF NOT EXISTS " + Tables.STATUS_TABLE.TABLE_NAME + " ( "
            + Tables.STATUS_TABLE.LOCAL_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Tables.STATUS_TABLE.ID + " TEXT NOT NULL, "
            + Tables.STATUS_TABLE.USER_ID + " TEXT NOT NULL, "
            + Tables.STATUS_TABLE.LATITUDE + " REAL, "
            + Tables.STATUS_TABLE.LONGTITUDE + " REAL, "
            + Tables.STATUS_TABLE.NAME + " TEXT, "
            + Tables.STATUS_TABLE.TEXT + " TEXT, "
            + Tables.STATUS_TABLE.URL + " URL, "
            + Tables.STATUS_TABLE.START_DATE + " INTEGER NOT NULL, "
            + Tables.STATUS_TABLE.END_DATE + " INTEGER NOT NULL, "
            + Tables.STATUS_TABLE.LAST_OPEN_DATE + " INTEGER NOT NULL, "
            + Tables.STATUS_TABLE.CATEGORY + " TEXT NOT NULL, "
            + Tables.STATUS_TABLE.HASHTAGS + " TEXT NOT NULL, "
            + Tables.STATUS_TABLE.IMAGES + " TEXT NOT NULL);";

    public static final String createMessageTableScript = "CREATE TABLE IF NOT EXISTS " + Tables.MESSAGE_TABLE.TABLE_NAME + " ( "
            + Tables.MESSAGE_TABLE.LOCAL_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Tables.MESSAGE_TABLE.ID + " TEXT NOT NULL, "
            + Tables.MESSAGE_TABLE.USER_ID + " TEXT NOT NULL, "
            + Tables.MESSAGE_TABLE.STATUS_ID + " TEXT NOT NULL, "
            + Tables.MESSAGE_TABLE.TEXT + " TEXT, "
            + Tables.MESSAGE_TABLE.CREATED + " INTEGER NOT NULL, "
            + Tables.MESSAGE_TABLE.NICKNAME + " TEXT NOT NULL);";

}
