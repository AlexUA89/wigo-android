package com.wigo.android.core.database.datas;

import android.content.ContentValues;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Oleksii Khom
 * Date: 01.09.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class DBStorable implements Parcelable, Cloneable {

    public static final long DEFAULT_ROW_ID = -1l;

    protected long localId = DBStorable.DEFAULT_ROW_ID;

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public abstract int getTypeID();

    public abstract ContentValues getContentValues();

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

}
