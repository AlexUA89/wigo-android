package com.wigo.android.core.server.dto;

import android.support.annotation.NonNull;

import java.util.UUID;

public class MessageDto extends Dto {

    @NonNull
    private UUID id;

    @NonNull
    private UUID userId;

    @NonNull
    private String text;

    @NonNull
    private String created;

    public MessageDto() {
    }

    public MessageDto(@NonNull UUID id, @NonNull UUID userId, @NonNull String text, @NonNull String created) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.created = created;
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
    public String getCreated() {
        return created;
    }

    public void setCreated(@NonNull String created) {
        this.created = created;
    }
}
