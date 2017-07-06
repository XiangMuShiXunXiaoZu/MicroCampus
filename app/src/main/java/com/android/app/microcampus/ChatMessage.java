package com.android.app.microcampus;

/**
 * Created by lulujie on 2017/7/4.
 */

public class ChatMessage {
    public static final int MessageType_From = 1;
    public static final int MessageType_To = 0;
    private int mType;
    private String mContent;
    private int uid;

    public ChatMessage(int Type,String content,int uid){
        this.mType = Type;
        this.mContent = content;
        this.uid = uid;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setUid (int i){ uid = i; }

    public int getUid() {return uid;}

}
