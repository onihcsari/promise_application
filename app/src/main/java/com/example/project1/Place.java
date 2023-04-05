package com.example.project1;

import com.google.gson.annotations.SerializedName;

public class Place {
    @SerializedName("place_name")
    private String placeName;

    @SerializedName("latitude")
    private double x;

    @SerializedName("longitude")
    private double y;

    public String getPlaceName() {
        return placeName;
    }

    public String toString(){
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return x;
    }

    public void setLatitude(double latitude) {
        this.x = latitude;
    }

    public double getLongitude() {
        return y;
    }

    public void setLongitude(double longitude) {
        this.y = longitude;
    }
}