package com.wigo.android.core.server.dto;

import com.wigo.android.core.database.datas.Status;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by AlexUA89 on 10/13/2016.
 */
public class StatusDto extends Dto {

    private UUID id;
    private UUID userId;
    private double latitude;
    private double longitude;
    private String name;
    private String text;
    private Date startDate;
    private Date endDate;
    private String kind;
    private List<String> hashtags;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
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
        if (startDate != null ? !startDate.equals(statusDto.startDate) : statusDto.startDate != null)
            return false;
        if (endDate != null ? !endDate.equals(statusDto.endDate) : statusDto.endDate != null)
            return false;
        if (kind != null ? !kind.equals(statusDto.kind) : statusDto.kind != null) return false;
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
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        result = 31 * result + (hashtags != null ? hashtags.hashCode() : 0);
        return result;
    }
}
