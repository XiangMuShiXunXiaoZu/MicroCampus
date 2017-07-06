package com.android.app.microcampus;

import android.app.Application;

public class Data extends Application {
    private double latitude = 0.0f;
    private double longitude = 0.0f;
    private int userId = -1;
    private String nickname="";
    private String summary="";
    private String username="";

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

    public String getNickname(){ return nickname; }
    public void setNickname(String nickname){ this.nickname = nickname; }

    public String getSummary(){ return nickname; }
    public void setSummary(String summary){ this.summary = summary; }

    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }
}