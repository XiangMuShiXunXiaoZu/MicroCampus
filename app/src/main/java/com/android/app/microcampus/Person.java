package com.android.app.microcampus;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import butterknife.Bind;

import static android.content.Context.MODE_PRIVATE;
import static com.android.app.microcampus.R.id.action_search;
import static com.android.app.microcampus.R.id.action_update;
import static com.android.app.microcampus.R.id.user_nickname;


/**
 * A simple {@link Fragment} subclass.
 */
public class Person extends Fragment {
    private static final String TAG = "PersonActivity";
    private static final String url="http://123.206.125.253/getuserinfo"; //所需url
    private static RequestQueue requestQueue;
    private SharedPreferences user_info;
    private int userId;
    private TextView user_name;
    private TextView user_nickname;
    private TextView user_summary;
    private View view;
    private Activity ac;

    public Person() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (ac==null) Log.v("activity", "null");
        user_info = ac.getSharedPreferences("user_info", MODE_PRIVATE);
        userId = user_info.getInt("uid", -1);
        user_name = (TextView)view.findViewById(R.id.user_name);
        user_nickname = (TextView)view.findViewById(R.id.user_nickname);
        user_summary = (TextView)view.findViewById(R.id.user_summary);

        requestQueue = Volley.newRequestQueue(getActivity());

        //显示用户初始信息
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        HashMap<String, Integer> userMap = new HashMap<String, Integer>();
        userMap.put("userId", userId);
        JSONObject map=new JSONObject(userMap);
        JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST,url,map,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try{
                    int userId = response.getInt("userId");
                    if(userId >= 0) {
                        String username = response.getString("username");
                        String nickname = response.getString("nickname");
                        String summary = response.getString("summary");
                        if(username == null)
                            username = "";
                        if(nickname == null)
                            nickname = "";
                        if(summary == null)
                            summary = "";
                        user_name.setText(username);
                        user_nickname.setText(nickname);
                        user_summary.setText(summary);
                    }
                } catch (JSONException e) {
                    Log.e("ERROR", e.getMessage(), e);
                    //Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("ERROR", error.getMessage(), error);
                //Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(req);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ac = (MainActivity)context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.person_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case action_update:
                // Start the Update activity
                Log.d(TAG, "inside onclick");
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                bundle.putString("username", user_name.getText().toString());
                bundle.putString("summary", user_summary.getText().toString());
                bundle.putString("nickname", user_nickname.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), UpdateActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
