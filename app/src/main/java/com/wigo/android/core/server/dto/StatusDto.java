package com.wigo.android.core.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by AlexUA89 on 10/13/2016.
 */
public class StatusDto extends StatusSmallDto implements Dto{

    private UUID userId;
    private String text;
    private String url;
    private List<String> hashtags;
    private List<String> images = new ArrayList<>();

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

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusDto statusDto = (StatusDto) o;

        if (Double.compare(statusDto.latitude, latitude) != 0) return false;
        if (Double.compare(statusDto.longitude, longitude) != 0) return false;
        if (id != null ? !id.equals(statusDto.id) : statusDto.id != null) return false;
        if (userId != null ? !userId.equals(statusDto.userId) : statusDto.userId != null)
            return false;
        if (name != null ? !name.equals(statusDto.name) : statusDto.name != null) return false;
        if (text != null ? !text.equals(statusDto.text) : statusDto.text != null) return false;
        if (getStartDate() != null ? !getStartDate().equals(statusDto.getStartDate()) : statusDto.getStartDate() != null)
            return false;
        if (getEndDate() != null ? !getEndDate().equals(statusDto.getEndDate()) : statusDto.getEndDate() != null)
            return false;
        return hashtags != null ? hashtags.equals(statusDto.hashtags) : statusDto.hashtags == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (getStartDate() != null ? getStartDate().hashCode() : 0);
        result = 31 * result + (getEndDate() != null ? getEndDate().hashCode() : 0);
        result = 31 * result + (hashtags != null ? hashtags.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public LatLng getPosition() {
       return new LatLng(latitude, longitude);
    }
}
