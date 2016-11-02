package com.wigo.android.core.database.datas;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.text.TextUtils;

import com.wigo.android.core.AppLog;
import com.wigo.android.core.database.constants.DBConstants;
import com.wigo.android.core.database.constants.Tables;
import com.wigo.android.core.server.dto.StatusKind;
import com.wigo.android.core.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Status extends DBStorable {

    public static final int TypeID = 1;
    private static final String TAG = Status.class.getCanonicalName();
    private UUID id = DBConstants.DEFAULT_ROW_ID;
    private UUID userId;
    private double latitude;
    private double longitude;
    private String name;
    private String text;
    private Date startDate;
    private Date endDate;
    private String kind;
    private Date lastOpenDate;

    public Status(UUID id, UUID userId, double latitude, double longitude, String name, String text, Date startDate, Date endDate, String kind, Date lastOpenDate) {
        this.id = id;
        if (userId == DBConstants.DEFAULT_ROW_ID) {
            throw new IllegalArgumentException("userId can not be " + userId);
        }
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.text = text;
        if (startDate == null) throw new IllegalArgumentException("startDate can not be null");
        this.startDate = startDate;
        if (endDate == null) throw new IllegalArgumentException("endDate can not be null");
        this.endDate = endDate;
        if (kind == null) throw new IllegalArgumentException("kind can not be null");
        this.kind = kind;
        this.lastOpenDate = lastOpenDate;
    }

    public Status(UUID userId, double latitude, double longitude, String name, String text, Date startDate, Date endDate, String kind, Date lastOpenDate) {
        this(null, userId, latitude, longitude, name, text, startDate, endDate, kind, lastOpenDate);
    }

    public Status(Status status) {
        this(status.getId(), status.getUserId(), status.getLatitude(), status.getLongitude(), status.getName(), status.getText(), status.getStartDate(), status.getEndDate(), status.getKind(), status.getLastOpenDate());
    }

    public Status(Parcel in) {
        this(UUID.fromString(in.readString()), UUID.fromString(in.readString()), in.readDouble(), in.readDouble(), in.readString(), in.readString(), (java.util.Date) in.readSerializable(), (java.util.Date) in.readSerializable(), in.readString(), (java.util.Date) in.readSerializable());
    }

    public Status(Cursor c) throws ParseException {
        this(UUID.fromString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.ID))),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.USER_ID))),
                c.getDouble(c.getColumnIndex(Tables.STATUS_TABLE.LATITUDE)),
                c.getDouble(c.getColumnIndex(Tables.STATUS_TABLE.LONGTITUDE)),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.NAME)),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.TEXT)),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.START_DATE))),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.END_DATE))),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.KIND)),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.LAST_OPEN_DATE))));
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public int getTypeID() {
        return TypeID;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tables.STATUS_TABLE.USER_ID, userId.toString());
        values.put(Tables.STATUS_TABLE.LATITUDE, latitude);
        values.put(Tables.STATUS_TABLE.LONGTITUDE, longitude);
        values.put(Tables.STATUS_TABLE.NAME, name);
        values.put(Tables.STATUS_TABLE.TEXT, text);
        values.put(Tables.STATUS_TABLE.START_DATE, startDate.getTime());
        values.put(Tables.STATUS_TABLE.END_DATE, endDate.getTime());
        values.put(Tables.STATUS_TABLE.KIND, kind);
        if (lastOpenDate == null) {
            values.put(Tables.STATUS_TABLE.LAST_OPEN_DATE, new Date().getTime());
        } else {
            values.put(Tables.STATUS_TABLE.LAST_OPEN_DATE, lastOpenDate.getTime());
        }
        return values;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Status(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(userId.toString());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(text);
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeString(kind);
        dest.writeSerializable(lastOpenDate);
    }

    public static final Creator CREATOR = new Creator() {
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Status)) {
            return false;
        }
        Status o1 = (Status) o;
        return !(id != null && o1.getId() != null && !id.equals(o1.getId()))
                && o1.getName().equalsIgnoreCase(name)
                && o1.getText().equalsIgnoreCase(text)
                && o1.getUserId().equals(userId)
                && o1.getLatitude() == latitude
                && o1.getLongitude() == longitude;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Date getLastOpenDate() {
        return lastOpenDate;
    }

    public void setLastOpenDate(Date lastOpenDate) {
        this.lastOpenDate = lastOpenDate;
    }
}
