package com.android.app.microcampus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lulujie on 2017/7/4.
 */

public class ChatAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChatMessage> mData;

    public ChatAdapter(Context context,List<ChatMessage> data){
        this.mContext = context;
        this.mData = data;
    }

    public void Refresh()
    {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int Index) {
        return mData.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
        ImageView image;
        switch(mData.get(Index).getType())
        {
            case ChatMessage.MessageType_From:
                mView=LayoutInflater.from(mContext).inflate(R.layout.layout_from, null);
                Content=(TextView)mView.findViewById(R.id.fromContent);
                image = (ImageView)mView.findViewById(R.id.fromHeader);
                Content.setText(mData.get(Index).getContent());
                break;
            case ChatMessage.MessageType_To:
                mView=LayoutInflater.from(mContext).inflate(R.layout.layout_to, null);
                Content=(TextView)mView.findViewById(R.id.toContent);
                image = (ImageView)mView.findViewById(R.id.toHeader);
                Content.setText(mData.get(Index).getContent());
                break;
        }
        return mView;
    }
}
