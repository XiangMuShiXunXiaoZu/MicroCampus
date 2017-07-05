package com.android.app.microcampus;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
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
//        HashMap<String, Object> map1 = new HashMap<String, Object>();
//        map1.put("image",null);
//        map1.put("username", "小明");
//        map1.put("message", "淡定");
//        listmsg.add(map1);
//        HashMap<String, Object> map2 = new HashMap<String, Object>();
//        map2.put("image",null);
//        map2.put("username", "小王");
//        map2.put("message", "淡定");
//        listmsg.add(map2);
        requestQueue = Volley.newRequestQueue(getContext());

        HashMap<String, Object>userMap=new HashMap<String, Object>();
        userMap.put("userId", app.getUserId());
        JSONObject map=new JSONObject(userMap);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
//                    listmsg.clear();
                    int len = response.getInt("messageNumber");
                    if (len <= 0) {
                        return;
                    }
                    databaseAdapter dbAdapter = new databaseAdapter(getContext(), "message1", null, 3);
                    Log.d("DatabaseName", dbAdapter.getDatabaseName());
                    SQLiteDatabase db = dbAdapter.getWritableDatabase();
                    for(int i = 0; i < len; i++) {
                        HashMap<String, Object> listem = new HashMap<String, Object>();
                        String nickname = response.getString("nickname" + i);
                        String message = response.getString("message" + i);
                        String sendUserId = response.getString("sendUserId" + i);
                        listem.put("userId", response.getString("sendUserId" + i));
                        listem.put("username", response.getString("nickname" + i));
                        db.execSQL("INSERT INTO chatting (sendUserId, nickname, message) VALUES (" +
                                sendUserId + ", \"" + nickname + "\", \"" + message + "\");");
                        Cursor cursor = db.rawQuery("SELECT message FROM chatting WHERE nickname = \"" +
                                      nickname + "\";"
                             , null);
                        cursor.moveToFirst();
                        Toast.makeText(getContext(),cursor.getString(0),Toast.LENGTH_SHORT).show();

                        listem.put("message", response.getString("message" + i));
                        listem.put("image", null);
                        listmsg.add(listem);
                        initMsg();
                    }
                }catch (JSONException e){
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(getContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                }
            }},new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.getMessage(), error);
                Toast.makeText(getContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();

            }
        }
        );
        requestQueue.add(req);

        return view;
    }

    public void initMsg(){
        myAdapter = new SimpleAdapter(getContext(),
                listmsg,
                R.layout.list_message,
                new String[]{"username","message","image"},
                new int[]{R.id.nameText,R.id.messageText,R.id.messageImageView});
        listView.setAdapter(myAdapter);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Bundle bundle = new Bundle();
                bundle.putInt("userId", Integer.valueOf(listmsg.get(arg2).get("userId").toString()));
                bundle.putString("nickname", (listmsg.get(arg2).get("nickname").toString()));

                Intent intent = new Intent();
                intent.setClass(getContext(), ChatActivity.class);

                startActivity(intent);
            }
        });

    }
    class databaseAdapter extends SQLiteOpenHelper {
        public databaseAdapter(Context context, String name,
                               SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            Log.d("Create sql"  ,"CREATE TABLE chatting (" +
                    "nickname   VARCHAR(30) NOT NULL," +
                    "sendUserId    INT     NOT NULL," +
                    "message   VARCHAR(200)" +
                    ");");
            db.execSQL("CREATE TABLE chatting (" +
                    "nickname   VARCHAR(30) NOT NULL," +
                    "sendUserId    INT     NOT NULL," +
                    "message   VARCHAR(200)" +
                    ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }
}
