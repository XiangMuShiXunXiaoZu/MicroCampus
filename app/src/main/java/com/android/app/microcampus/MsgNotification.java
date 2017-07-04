package com.android.app.microcampus;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_msg_notification, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("image", R.drawable.ic_home_black_24dp);
        map.put("username", "一四又三");
        map.put("message", "陈树：淡定");
        listmsg.add(map);

        myAdapter = new SimpleAdapter(getContext(),
                listmsg,
                R.layout.list_message,
                new String[]{"username","message","image"},
                new int[]{R.id.nameText,R.id.messageText,R.id.imageView});
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                Intent intent = new Intent();
                intent.setClass(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }


}
