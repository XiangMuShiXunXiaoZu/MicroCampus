package com.android.app.microcampus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by welcome on 2017/7/4.
 */

public class UpdateActivity extends AppCompatActivity {
    private static final String TAG = "UpdateActivity";
    private static final String url="http://123.206.125.253/updateuserinfo"; //所需url
    private static RequestQueue requestQueue;

    @Bind(R.id.update_name) EditText _nameText;
    @Bind(R.id.update_nickname) EditText _nicknameText;
    @Bind(R.id.update_summary) EditText _summaryText;
    @Bind(R.id.btn_update) Button _updateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        final int userId = bundle.getInt("userId");
        _nameText.setText(bundle.getString("username"));
        _nicknameText.setText(bundle.getString("nickname"));
        _summaryText.setText(bundle.getString("summary"));

        requestQueue = Volley.newRequestQueue(this);

        _updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示用户初始信息
                final ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("更新用户信息中...");
                progressDialog.show();

                String username = _nameText.getText().toString();
                String nickname = _nicknameText.getText().toString();
                String summary = _summaryText.getText().toString();

                HashMap<String, String> newMap = new HashMap<String, String>();
                newMap.put("userId", String.valueOf(userId));
                newMap.put("username", username);
                newMap.put("nickname", nickname);
                newMap.put("summary", summary);

                JSONObject map=new JSONObject(newMap);
                JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST,url,map,new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        progressDialog.dismiss();
                        try{
                            int isUpdate = response.getInt("isUpdate");
                            if (isUpdate >= 0){
                                Toast.makeText(getBaseContext(), "更新用户信息成功", Toast.LENGTH_SHORT).show();
                            }else {

                                Toast.makeText(getBaseContext(), "用户信息上传有误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage(), e);
                            Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialog.dismiss();
                        Log.e("ERROR", error.getMessage(), error);
                        Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(req);


                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("summary", summary);
                bundle.putString("nickname", nickname);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
