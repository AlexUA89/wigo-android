package com.wigo.android.core.server.dto;

import java.util.UUID;

/**
 * Created by AlexUA89 on 11/2/2016.
 */

public class WigoUserInfoDto {

    private UUID id;
    private String nickname;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
