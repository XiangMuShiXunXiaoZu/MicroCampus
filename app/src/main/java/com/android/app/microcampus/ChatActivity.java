package com.android.app.microcampus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.android.app.microcampus.R.id.action_release;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout BtnSend;
    private EditText InputBox;
    private List<ChatMessage> mData = new ArrayList<ChatMessage>();;
    private ChatAdapter mAdapter;
    private int userId = -1, sendId = -1;
    private String nickname;
    private static RequestQueue requestQueue;
    private static final String messageUrl="http://123.206.125.253/addmessage"; //所需url
    private Data app;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestQueue = Volley.newRequestQueue(this);

        Bundle bundle = getIntent().getExtras();
        nickname = bundle.getString("nickname");
        getSupportActionBar().setTitle(nickname);
        sendId = bundle.getInt("sendId");

        app = (Data)getApplication();
        userId = app.getUserId();

        mListView=(ListView)findViewById(R.id.MainList);
        LoadData(mData);
        mAdapter = new ChatAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mData.size());
        InputBox=(EditText)findViewById(R.id.InputBox);
        BtnSend=(LinearLayout)findViewById(R.id.BtnSend);

        handler.postDelayed(myRunnable, 2000);

        InputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()!=0){
                    BtnSend.setBackgroundColor(getColor(R.color.accentLight));
                }else{
                    BtnSend.setBackgroundColor(getColor(R.color.iron));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        BtnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(!InputBox.getText().toString().equals(""))
                {
                    //构造输入消息
                    ChatMessage message=new ChatMessage(ChatMessage.MessageType_To,InputBox.getText().toString());
                    mData.add(message);
                    HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
                    itemMap.put("sendUserId", userId);
                    itemMap.put("receiveUserId", sendId);
                    itemMap.put("message",message.getContent());
                    JSONObject map=new JSONObject(itemMap);
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,messageUrl,map,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                        }
                    });
                    requestQueue.add(req);

                    databaseAdapter dbAdapter = new databaseAdapter(getApplicationContext(), "message", null, 3);
                    SQLiteDatabase db = dbAdapter.getWritableDatabase(); //读
                    db.execSQL("INSERT INTO chatting (sendId, type, nickname, message) VALUES (" +
                            sendId + "," + 0 + ", \"" + nickname + "\", \"" + message.getContent() + "\");");
                    //更新数据
                    mAdapter.Refresh();
                }
                //清空输入框
                InputBox.setText("");
                //关闭输入法
                imm.hideSoftInputFromWindow(null, InputMethodManager.HIDE_IMPLICIT_ONLY);
                //滚动列表到当前消息
                mListView.smoothScrollToPositionFromTop(mData.size(), 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Handler handler = new Handler();

    private Runnable myRunnable= new Runnable() {
        public void run() {
            mData.clear();
            LoadData(mData);
            mAdapter.Refresh();
            handler.postDelayed(myRunnable, 2000);
        }
    };

    private void LoadData(List<ChatMessage> mData)
    {
        databaseAdapter dbAdapter = new databaseAdapter(getApplicationContext(), "message", null, 3);
        SQLiteDatabase db = dbAdapter.getReadableDatabase(); //读

        Cursor cursor = db.rawQuery("SELECT message, type FROM chatting WHERE sendId = " +
                sendId + ";", null);

        int num = cursor.getCount();
        cursor.moveToFirst();

        for(int i = 0; i < num; i++) {
            ChatMessage Message = new ChatMessage(cursor.getInt(cursor.getColumnIndex("type")), cursor.getString(cursor.getColumnIndex("message")));
            mData.add(Message);
            cursor.moveToNext();
        }

        cursor.close();
    }

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

