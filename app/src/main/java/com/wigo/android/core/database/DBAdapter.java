package com.wigo.android.core.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wigo.android.core.AppLog;
import com.wigo.android.core.database.constants.Tables;
import com.wigo.android.core.database.datas.DBStorable;
import com.wigo.android.core.database.datas.Message;
import com.wigo.android.core.database.datas.Status;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 29.08.13
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
class DBAdapter extends Database {

    public static final String TAG = DBAdapter.class.getCanonicalName();
    private static DatabaseHelper helper;
    private static int connectionCounter = 0;
    private static SQLiteDatabase db;

    public DBAdapter(DatabaseHelper helper) {
        this.helper = helper;
    }

    private synchronized static boolean openInternalDatabase() {
        try {
            db = helper.getWritableDatabase();
            return db.isOpen();
        } catch (Throwable e) {
            AppLog.E(TAG, e);
        }
        return false;
    }

    private synchronized static void closeInternalDatabase() {
        try {
            if (db != null) {
                db.close();
            }
        } catch (Throwable e) {
            AppLog.E(TAG, e);
        } finally {
            db = null;
        }
    }

    @Override
    public boolean open() {
        AppLog.D(TAG, "DBAdapter.open");
        if (connectionCounter > 0) {
            connectionCounter++;
            return true;
        } else {
            if (openInternalDatabase()) {
                connectionCounter++;
                return true;
            }
        }
        return false;
    }

    @Override
    synchronized public void close() {
        AppLog.D(TAG, "DBAdapter.close");
        if (connectionCounter > 1) {
            connectionCounter--;
        }
        if (connectionCounter == 1) {
            connectionCounter--;
            closeInternalDatabase();
        }
    }

    @Override
    public long insertNewDBStorable(DBStorable newDBStorable) {
        Object[] tableInfo = getTableInfo(newDBStorable.getTypeID());
        return db.insert((String) tableInfo[0], null, newDBStorable.getContentValues());
    }

    @Override
    public List<Long> insertNewDBStorables(List<DBStorable> listDBStorable) {
        List<Long> result = new ArrayList<Long>();
        for (DBStorable data : listDBStorable) {
            result.add(insertNewDBStorable(data));
        }
        return result;
    }

    @Override
    public int deleteDBStorableByType(int typeID, long rowID) {
        Object[] tableInfo = getTableInfo(typeID);
        return db.delete((String) tableInfo[0], "" + tableInfo[1] + " = " + rowID, null);
    }

    @Override
    public int deleteDBStorable(DBStorable data) {
        return deleteDBStorableByType(data.getTypeID(), data.getLocalId());
    }

    @Override
    public boolean updateDBStorable(DBStorable data) {
        Object[] tableInfo = getTableInfo(data.getTypeID());
        int temp = db.update((String) tableInfo[0], data.getContentValues(), "" + tableInfo[1] + " = " + data.getLocalId(), null);
        if (temp < 1) {
            return false;
        }
        return true;
    }


    @Override
    public Cursor selectAllDBStorablesByType(int typeId) {
        Object[] tableInfo = getTableInfo(typeId);
        Cursor c = null;
        c = db.rawQuery("SELECT * FROM " + tableInfo[0], null);
        return c;
    }

    @Override
    public Cursor selectAllLastActiveStatuses() {
        Object[] tableInfo = getTableInfo(Status.TypeID);
        return db.rawQuery("SELECT * FROM " + tableInfo[0] + " ORDER BY " + Tables.STATUS_TABLE.LAST_OPEN_DATE + " DESC", null);
    }

    @Override
    public Cursor selectMessagesForStatus(UUID statusId) {
        Object[] tableInfo = getTableInfo(Message.TypeID);
        return db.rawQuery("SELECT * FROM " + tableInfo[0] + " WHERE " + Tables.MESSAGE_TABLE.STATUS_ID + " = '" + statusId + "' ORDER BY " + Tables.MESSAGE_TABLE.CREATED, null);
    }

    @Override
    public DBStorable selectDBStorableByTypeAndId(int typeId, long dbstorableId) {
        Object[] tableInfo = getTableInfo(typeId);
        DBStorable result = null;
        Cursor c = null;
        c = db.rawQuery("SELECT * FROM " + tableInfo[0] + " WHERE " + tableInfo[1] + " = " + dbstorableId + ";", null);
        if (c.moveToFirst()) {
            result = parseFromCursor(c, typeId);
        }
        return result;
    }

    @Override
    public Status selectStatusServerById(UUID serverId) {
        Object[] tableInfo = getTableInfo(Status.TypeID);
        Cursor c = null;
        Status result = null;
        c = db.rawQuery("SELECT * FROM " + tableInfo[0] + " WHERE id = '" + serverId + "';", null);
        if (c.moveToFirst()) {
            result = (Status) parseFromCursor(c, Status.TypeID);
        }
        return result;
    }


    public DBStorable parseFromCursor(Cursor c, int typeId) {
        DBStorable result = null;
        try {
            switch (typeId) {
                case Status.TypeID:
                    try {
                        result = new Status(c);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case Message.TypeID:
                    try {
                        result = new Message(c);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("there is not any type with this id" + typeId);
            }
        } catch (IllegalArgumentException e) {
            AppLog.E(TAG, e);
        }
        return result;
    }


    /**
     * Get information about table
     * object[0] - table name
     * object[1] - column name of rowId
     *
     * @param typeId
     * @return
     */
    private Object[] getTableInfo(int typeId) {
        Object[] result = new Object[5];
        try {
            switch (typeId) {
                case Status.TypeID:
                    result[0] = Tables.STATUS_TABLE.TABLE_NAME;
                    result[1] = Tables.STATUS_TABLE.LOCAL_ID;
                    break;
                case Message.TypeID:
                    result[0] = Tables.MESSAGE_TABLE.TABLE_NAME;
                    result[1] = Tables.MESSAGE_TABLE.LOCAL_ID;
                    break;
                default:
                    throw new IllegalArgumentException("there is not any type with this id" + typeId);
            }
        } catch (IllegalArgumentException e) {
            AppLog.E(TAG, e);
        }
        return result;
    }
}
