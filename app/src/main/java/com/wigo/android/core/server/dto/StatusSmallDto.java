package com.wigo.android.core.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/20/2016.
 */

public class StatusSmallDto implements Dto, ClusterItem {

    protected UUID id;
    protected String name;
    protected double latitude;
    protected double longitude;
    protected String category;
    private Date startDate;
    private Date endDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(StatusCategory category) {
        this.category = category.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @JsonIgnore
    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @JsonIgnore
    @Override
    public String getTitle() {
        return name;
    }

    @JsonIgnore
    @Override
    public String getSnippet() {
        return name + " " + startDate + " " + endDate;
    }

}
