package com.android.app.microcampus;

import java.util.Date;

/**
 * Created by lulujie on 2017/6/20.
 */

public class Item {
    private String mName;
    private Date mDate;
    private String mDescription;

    public Item(){
        mDate = new Date();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
