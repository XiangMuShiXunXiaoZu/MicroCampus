package com.android.app.microcampus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Button BtnSend;
    private EditText InputBox;
    private List<ChatMessage> mData;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        final ListView mListView=(ListView)findViewById(R.id.MainList);
        mData=LoadData();
        mAdapter=new ChatAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.smoothScrollToPositionFromTop(mData.size(), 0);
        InputBox=(EditText)findViewById(R.id.InputBox);
        BtnSend=(Button)findViewById(R.id.BtnSend);

        BtnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(InputBox.getText().toString()!="")
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

        ChatMessage Message=new ChatMessage(ChatMessage.MessageType_Time,"2013年12月27日");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.MessageType_From,"山重水复疑无路");
        Messages.add(Message);

        Message=new ChatMessage(ChatMessage.MessageType_To,"柳暗花明又一村");
        Messages.add(Message);

        return Messages;
    }




}

