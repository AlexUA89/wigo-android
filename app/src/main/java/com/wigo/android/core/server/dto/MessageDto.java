package com.wigo.android.core.server.dto;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

public class MessageDto implements Dto {

    @NonNull
    private UUID id;

    @NonNull
    private UUID userId;

    @NonNull
    private String text;

    @NonNull
    private Date created;

    @NonNull
    private String nickname;

    public MessageDto() {
    }

    public MessageDto(@NonNull UUID id, @NonNull UUID userId, @NonNull String text, @NonNull Date created, @NonNull String nickname) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.created = created;
        this.nickname = nickname;
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    @NonNull
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(@NonNull UUID userId) {
        this.userId = userId;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    @NonNull
    public Date getCreated() {
        return created;
    }

    public void setCreated(@NonNull Date created) {
        this.created = created;
    }

    @NonNull
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@NonNull String nickname) {
        this.nickname = nickname;
    }
}
