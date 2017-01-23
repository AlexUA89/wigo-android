package com.wigo.android.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.database.constants.CreateDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 29.08.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 2;

    public static String getDBPath() {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return ContextProvider.getAppContext().getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        } else {
            return "/data/data/" + ContextProvider.getAppContext().getPackageName() + "/databases/" + DATABASE_NAME;
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateDatabase.createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                up1to2(db);
        }
    }

    private void up1to2(SQLiteDatabase db) {
        db.execSQL("UPDATE status_table SET category = 'chat' WHERE category is NULL OR kind = 'chat'");
        db.execSQL("CREATE TABLE IF NOT EXISTS status_table_temp ( local_id INTEGER PRIMARY KEY NOT NULL, id TEXT NOT NULL, user_id TEXT NOT NULL, latitude REAL, longitude REAL, name TEXT, text TEXT, url URL, start_date INTEGER NOT NULL, end_date INTEGER NOT NULL, last_open_date INTEGER NOT NULL, category TEXT NOT NULL, hashtags TEXT NOT NULL, images TEXT NOT NULL)");
        db.execSQL("INSERT INTO status_table_temp SELECT local_id,id, user_id, latitude, longitude, name, text, url, start_date, end_date, last_open_date, category TEXT, hashtags TEXT, images TEXT FROM status_table");
        db.execSQL("DROP TABLE status_table");
        db.execSQL("ALTER TABLE status_table_temp RENAME TO status_table");
    }
}
