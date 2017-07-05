package com.android.app.microcampus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout BtnSend;
    private EditText InputBox;
    private List<ChatMessage> mData;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        databaseAdapter dbAdapter = new databaseAdapter(getApplicationContext(), "message1", null, 3);
        Log.d("DatabaseName", dbAdapter.getDatabaseName());
        SQLiteDatabase db = dbAdapter.getWritableDatabase();
        Bundle bundle = getIntent().getExtras();


        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle("");
        final ListView mListView=(ListView)findViewById(R.id.MainList);
        mData=LoadData();
        mAdapter=new ChatAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.smoothScrollToPositionFromTop(mData.size(), 0);
        InputBox=(EditText)findViewById(R.id.InputBox);
        BtnSend=(LinearLayout)findViewById(R.id.BtnSend);

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
                    //获取时间
                    Calendar c=Calendar.getInstance();
                    StringBuilder mBuilder=new StringBuilder();
                    mBuilder.append(Integer.toString(c.get(Calendar.YEAR))+"年");
                    mBuilder.append(Integer.toString(c.get(Calendar.MONTH))+"月");
                    mBuilder.append(Integer.toString(c.get(Calendar.DATE))+"日");
                    mBuilder.append(Integer.toString(c.get(Calendar.HOUR_OF_DAY))+":");
                    mBuilder.append(Integer.toString(c.get(Calendar.MINUTE)));
                    //构造时间消息
                    ChatMessage Message=new ChatMessage(ChatMessage.MessageType_Time,mBuilder.toString());
                    mData.add(Message);
                    //构造输入消息
                    Message=new ChatMessage(ChatMessage.MessageType_To,InputBox.getText().toString());
                    mData.add(Message);
                    //构造返回消息，如果这里加入网络的功能，那么这里将变成一个网络机器人
                    Message=new ChatMessage(ChatMessage.MessageType_From,"收到！");
                    mData.add(Message);
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

    private List<ChatMessage> LoadData()
    {
        List<ChatMessage> Messages=new ArrayList<ChatMessage>();

        ChatMessage Message=new ChatMessage(ChatMessage.MessageType_From,"山重水复疑无路");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.MessageType_To,"柳暗花明又一村");
        Messages.add(Message);




        return Messages;
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

