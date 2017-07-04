package com.android.app.microcampus;

import android.app.Application;

public class Data extends Application {
    private int userId = -1;
    private double latitude = 0.0f;
    private double longitude = 0.0f;

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

    public int getUserId(){
        return userId;
    }
    public void setUserId(int uid){
        userId = uid;
    }
}