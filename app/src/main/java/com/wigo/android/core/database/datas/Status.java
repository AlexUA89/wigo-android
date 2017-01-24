package com.wigo.android.core.database.datas;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.wigo.android.core.database.constants.Tables;
import com.wigo.android.core.server.dto.StatusCategory;
import com.wigo.android.ui.elements.CategoriesProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Status extends DBStorable {

    private static final String SPLITER = " ";

    public static final int TypeID = 1;
    private static final String TAG = Status.class.getCanonicalName();
    private UUID id;
    private UUID userId;
    private double latitude;
    private double longitude;
    private String name;
    private String text;
    private Date startDate;
    private Date endDate;
    private String url;
    private Date lastOpenDate;
    private List<String> hashtags;
    private String category;
    private List<String> images;

    private Status(long localId, UUID id, UUID userId, double latitude, double longitude, String name, String text, String url, Date startDate, Date endDate, String category, List<String> hashtags, List<String> images, Date lastOpenDate) {
        if (localId < DBStorable.DEFAULT_ROW_ID) {
            throw new IllegalArgumentException("localId can not be " + localId);
        }
        this.localId = localId;
        if (id == null) {
            throw new IllegalArgumentException("id can not be " + id);
        }
        this.id = id;
        if (userId == null) {
            throw new IllegalArgumentException("userId can not be " + userId);
        }
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.text = text;
        this.url = url;
        if (startDate == null) throw new IllegalArgumentException("startDate can not be null");
        this.startDate = startDate;
        if (endDate == null) throw new IllegalArgumentException("endDate can not be null");
        this.endDate = endDate;
        this.lastOpenDate = lastOpenDate;
        this.category = CategoriesProvider.OTHER;
        if (category != null) this.category = category;
        this.hashtags = new ArrayList<>();
        if (hashtags != null) this.hashtags = hashtags;
        this.images = new ArrayList<>();
        if (images != null) this.images = images;
    }

    public Status(UUID id, UUID userId, double latitude, double longitude, String name, String text, String url, Date startDate, Date endDate, String category, List<String> hashtags, List<String> images, Date lastOpenDate) {
        this(DBStorable.DEFAULT_ROW_ID, id, userId, latitude, longitude, name, text, url, startDate, endDate, category, hashtags, images, lastOpenDate);
    }

    public Status(Status status) {
        this(status.getLocalId(), status.getId(), status.getUserId(), status.getLatitude(), status.getLongitude(), status.getName(), status.getText(), status.getUrl(),
                status.getStartDate(), status.getEndDate(), status.getCategory().toString(), status.getHashtags(), status.getImages(), status.getLastOpenDate());
    }

    public Status(Parcel in) {
        this(in.readLong(), UUID.fromString(in.readString()), UUID.fromString(in.readString()), in.readDouble(), in.readDouble(), in.readString(), in.readString(), in.readString(),
                (java.util.Date) in.readSerializable(), (java.util.Date) in.readSerializable(), in.readString(), splitString(in.readString()), splitString(in.readString()), (java.util.Date) in.readSerializable());
    }

    public Status(Cursor c) throws ParseException {
        this(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.LOCAL_ID)),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.ID))),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.USER_ID))),
                c.getDouble(c.getColumnIndex(Tables.STATUS_TABLE.LATITUDE)),
                c.getDouble(c.getColumnIndex(Tables.STATUS_TABLE.LONGTITUDE)),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.NAME)),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.TEXT)),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.URL)),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.START_DATE))),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.END_DATE))),
                c.getString(c.getColumnIndex(Tables.STATUS_TABLE.CATEGORY)),
                splitString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.HASHTAGS))),
                splitString(c.getString(c.getColumnIndex(Tables.STATUS_TABLE.IMAGES))),
                new Date(c.getLong(c.getColumnIndex(Tables.STATUS_TABLE.LAST_OPEN_DATE))));
    }

    @Override
    public int getTypeID() {
        return TypeID;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tables.STATUS_TABLE.ID, id.toString());
        values.put(Tables.STATUS_TABLE.USER_ID, userId.toString());
        values.put(Tables.STATUS_TABLE.LATITUDE, latitude);
        values.put(Tables.STATUS_TABLE.LONGTITUDE, longitude);
        values.put(Tables.STATUS_TABLE.NAME, name);
        values.put(Tables.STATUS_TABLE.TEXT, text);
        values.put(Tables.STATUS_TABLE.URL, url);
        values.put(Tables.STATUS_TABLE.START_DATE, startDate.getTime());
        values.put(Tables.STATUS_TABLE.END_DATE, endDate.getTime());
        values.put(Tables.STATUS_TABLE.CATEGORY, category);
        values.put(Tables.STATUS_TABLE.HASHTAGS, listToString(hashtags));
        values.put(Tables.STATUS_TABLE.IMAGES, listToString(images));
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
        dest.writeLong(localId);
        dest.writeString(id.toString());
        dest.writeString(userId.toString());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(text);
        dest.writeString(url);
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeString(category);
        dest.writeString(listToString(hashtags));
        dest.writeString(listToString(images));
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

    public Date getLastOpenDate() {
        return lastOpenDate;
    }

    public void setLastOpenDate(Date lastOpenDate) {
        this.lastOpenDate = lastOpenDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public StatusCategory getCategory() {
        StatusCategory cat = StatusCategory.valueOf(category.toUpperCase());
        if(cat!=null) return cat;
        return StatusCategory.OTHER;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    private static String listToString(List<String> list) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            r.append(list.get(i));
            if (i < list.size() - 1) {
                r.append(SPLITER);
            }
        }
        return r.toString();
    }

    private static List<String> splitString(String string) {
        return Arrays.asList(string.split(SPLITER));
    }
}
