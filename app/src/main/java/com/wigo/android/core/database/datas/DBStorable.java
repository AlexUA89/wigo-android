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

    public abstract UUID getId();

    public abstract int getTypeID();

    public abstract ContentValues getContentValues();

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

}
