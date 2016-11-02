package com.wigo.android.core.server.dto;

/**
 * Created by AlexUA89 on 11/2/2016.
 */

public class WigoUserInfoResponseDto {
    private WigoUserInfoDto user;
    private String token;

    public WigoUserInfoDto getUser() {
        return user;
    }

    public void setUser(WigoUserInfoDto user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
