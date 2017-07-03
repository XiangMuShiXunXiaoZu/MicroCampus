package com.android.app.microcampus;

import android.app.Application;

public class Data extends Application {
    private int userId = -1;
    public int getUserId(){
        return userId;
    }
    public void setUserId(int uid){
        userId = uid;
    }
}