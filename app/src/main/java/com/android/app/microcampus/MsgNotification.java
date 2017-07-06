package com.android.app.microcampus;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
    private static RequestQueue requestQueue;
    private int userId = -1;




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

        requestQueue = Volley.newRequestQueue(getContext());

        userId = app.getUserId();
        myRunnable.run();
        return view;
    }

    public void initMsg(){
        myAdapter = new SimpleAdapter(getContext(),
                listmsg,
                R.layout.list_message,
                new String[]{"nickname","message","image"},
                new int[]{R.id.nameText,R.id.messageText,R.id.messageImageView});
        listView.setAdapter(myAdapter);

        myAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object bitmapData, String s) {
                if(view instanceof ImageView && bitmapData instanceof Number){
                    ImageView i = (ImageView)view;
                    switch (((Integer) bitmapData).intValue()){
                        case 1:
                            i.setImageResource(R.mipmap.hd_1);
                            break;
                        case 2:
                            i.setImageResource(R.mipmap.hd_2);
                            break;
                        case 3:
                            i.setImageResource(R.mipmap.hd_3);
                            break;
                        case 4:
                            i.setImageResource(R.mipmap.hd_4);
                            break;
                    }
                    //i.setImageBitmap((Bitmap) bitmapData);
                    return true;
                }
                return false;
            }
        });

        myAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Bundle bundle = new Bundle();
                bundle.putInt("sendId", Integer.valueOf(listmsg.get(arg2).get("sendId").toString()));
                bundle.putString("nickname", listmsg.get(arg2).get("nickname").toString());

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private Handler handler = new Handler();

    private Runnable myRunnable= new Runnable() {
        public void run() {
            HashMap<String, Object>userMap=new HashMap<String, Object>();
            userMap.put("userId", userId);
            JSONObject map=new JSONObject(userMap);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
//                  listmsg.clear();
                        int len = response.getInt("messageNumber");
                        databaseAdapter dbAdapter = new databaseAdapter(getContext(), "message", null, 3);
                        Log.d("DatabaseName", dbAdapter.getDatabaseName());
                        SQLiteDatabase db = dbAdapter.getWritableDatabase(); //写

                        for(int i = 0; i < len; i++) {

                            String nickname = response.getString("nickname" + i);
                            String message = response.getString("message" + i);
                            int sendUserId = response.getInt("sendUserId" + i);
                            db.execSQL("INSERT INTO chatting (sendId, type, nickname, message) VALUES (" +
                                    sendUserId + "," + 1 + ", \"" + nickname + "\", \"" + message + "\");");
                        }

                        db = dbAdapter.getReadableDatabase(); //读

                        listmsg.clear();

                        Cursor cursor = db.rawQuery("SELECT DISTINCT sendId FROM chatting;", null);
                        int index = cursor.getCount();
                        cursor.moveToFirst();

                        for (int i =0;i<index;i++) {
                            HashMap<String, Object> listem = new HashMap<String, Object>();
                            Cursor myCursor = db.rawQuery("SELECT message, nickname FROM chatting WHERE sendId = " +
                                    cursor.getInt(cursor.getColumnIndex("sendId")) + ";", null);
                            myCursor.moveToLast();
                            listem.put("image", cursor.getInt(cursor.getColumnIndex("sendId")));
                            listem.put("sendId", cursor.getInt(cursor.getColumnIndex("sendId")));
                            listem.put("message", myCursor.getString(myCursor.getColumnIndex("message")));
                            listem.put("nickname", myCursor.getString(myCursor.getColumnIndex("nickname")));
                            listmsg.add(listem);
                            myCursor.close();
                            cursor.moveToNext();
                        }
                        cursor.close();
                        initMsg();
                        handler.postDelayed(myRunnable, 5000);

                    }catch (JSONException e){
                        Log.e("ERROR", e.getMessage(), e);
                        handler.postDelayed(myRunnable, 5000);
                    }
                }},new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", error.getMessage(), error);
                    handler.postDelayed(myRunnable, 5000);
                }
            });
            requestQueue.add(req);
        }
    };

    class databaseAdapter extends SQLiteOpenHelper {
        public databaseAdapter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            Log.d("Create sql"  ,"CREATE TABLE");
            db.execSQL("CREATE TABLE chatting (" +
                    "nickname VARCHAR(30) NOT NULL," +
                    "sendId INT NOT NULL," +
                    "type INT NOT NULL," +
                    "message VARCHAR(200)" +
                    ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }
}
