package com.android.app.microcampus;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MsgNotification extends Fragment {

    private SimpleAdapter myAdapter;
    private ArrayList<HashMap<String, Object>> listmsg = new ArrayList<HashMap<String, Object>>();
    private ListView listView;
    private static final String url="http://123.206.125.253/checkmessage"; //所需url
    private Data app;



    public MsgNotification() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Data)getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_msg_notification, container, false);

        listView = (ListView)view.findViewById(R.id.listView);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("image", R.drawable.ic_home_black_24dp);
//        map.put("username", "一四又三");
//        map.put("message", "陈树：淡定");
//        listmsg.add(map);

        HashMap<Object, Object>userMap=new HashMap<Object, Object>();
        userMap.put("userId", app.getUserId());
        JSONObject map=new JSONObject(userMap);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                int userId = response.getInt("userId");

                }catch (JSONException e){
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(getContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();

                }


            }},new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );

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
