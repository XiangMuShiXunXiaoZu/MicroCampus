package com.android.app.microcampus;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MsgNotification extends Fragment {

    private SimpleAdapter myAdapter;
    private ArrayList<HashMap<String, Object>> listmsg = new ArrayList<HashMap<String, Object>>();
    private ListView listView;


    public MsgNotification() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_msg_notification, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("username", "一四又三");
        map.put("message", "陈树：淡定");
        map.put("image", null);
        listmsg.add(map);

        myAdapter = new SimpleAdapter(getContext(),
                listmsg,
                R.layout.list_message,
                new String[]{"username","message","image"},
                new int[]{R.id.nameText,R.id.messageText,R.id.messageImageView});
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        myAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object bitmapData, String s) {
                if(view instanceof ImageView && bitmapData instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) bitmapData);
                    return true;
                }
                return false;
            }
        });

        return view;
    }


}
