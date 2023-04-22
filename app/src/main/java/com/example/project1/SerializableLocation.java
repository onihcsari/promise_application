package com.example.project1;

import java.io.Serializable;

public class SerializableLocation implements Serializable {
    private double latitude;
    private double longitude;

    public SerializableLocation() {}

    public SerializableLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}