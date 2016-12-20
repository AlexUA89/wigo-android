package com.wigo.android.core.database.datas;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.wigo.android.core.database.constants.Tables;
import com.wigo.android.core.server.dto.MessageDto;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/18/2016.
 */

public class Message extends DBStorable {

    public static final int TypeID = 2;
    private static final String TAG = Message.class.getCanonicalName();

    private UUID id;
    private UUID userId;
    private String text;
    private Date created;
    private String nickname;
    private UUID statusId;

    public Message(MessageDto m, UUID statusId) {
        this(m.getId(), m.getUserId(), m.getText(), m.getCreated(), m.getNickname(), statusId);
    }

    public Message(long localId, UUID id, UUID userId, String text, Date created, String nickname, UUID statusId) {
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
        this.text = text;
        if (this.text == null) this.text = "";
        if (created == null) throw new IllegalArgumentException("created can not be null");
        this.created = created;
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("nickname can not be null or empty");
        }
        this.nickname = nickname;
        if (statusId == null) {
            throw new IllegalArgumentException("status can not be " + statusId);
        }
        this.statusId = statusId;
    }

    public Message(UUID id, UUID userId, String text, Date created, String nickname, UUID statusId) {
        this(DBStorable.DEFAULT_ROW_ID, id, userId, text, created, nickname, statusId);
    }

    public Message(Message m) {
        this(m.getLocalId(), m.getId(), m.getUserId(), m.getText(), m.getCreated(), m.getNickname(), m.getStatusId());
    }

    public Message(Parcel in) {
        this(in.readLong(), UUID.fromString(in.readString()), UUID.fromString(in.readString()), in.readString(), (java.util.Date) in.readSerializable(), in.readString(), UUID.fromString(in.readString()));
    }

    public Message(Cursor c) throws ParseException {
        this(c.getLong(c.getColumnIndex(Tables.MESSAGE_TABLE.LOCAL_ID)),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.MESSAGE_TABLE.ID))),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.MESSAGE_TABLE.USER_ID))),
                c.getString(c.getColumnIndex(Tables.MESSAGE_TABLE.TEXT)),
                new Date(c.getLong(c.getColumnIndex(Tables.MESSAGE_TABLE.CREATED))),
                c.getString(c.getColumnIndex(Tables.MESSAGE_TABLE.NICKNAME)),
                UUID.fromString(c.getString(c.getColumnIndex(Tables.MESSAGE_TABLE.STATUS_ID))));
    }

    @Override
    public int getTypeID() {
        return TypeID;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tables.MESSAGE_TABLE.ID, id.toString());
        values.put(Tables.MESSAGE_TABLE.USER_ID, userId.toString());
        values.put(Tables.MESSAGE_TABLE.TEXT, text);
        values.put(Tables.MESSAGE_TABLE.CREATED, created.getTime());
        values.put(Tables.MESSAGE_TABLE.NICKNAME, nickname);
        values.put(Tables.MESSAGE_TABLE.STATUS_ID, statusId.toString());
        return values;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Message(this);
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
        dest.writeString(text);
        dest.writeSerializable(created);
        dest.writeString(nickname);
        dest.writeString(statusId.toString());
    }

    public static final Creator CREATOR = new Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Message)) {
            return false;
        }
        Message o1 = (Message) o;
        return !(id != null && o1.getId() != null && !id.equals(o1.getId()))
                && o1.getText().equalsIgnoreCase(text)
                && o1.getStatusId().equals(statusId)
                && o1.getUserId().equals(userId);
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }
}
