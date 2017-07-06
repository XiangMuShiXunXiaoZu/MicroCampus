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
    private SharedPreferences user_info;
    private static RequestQueue requestQueue;
    private int userId = -1;
    private static final String url="http://123.206.125.253/getuserinfo"; //所需url
    private TextView userNickname;
    private TextView userSummary;
    private TextView userName;
    private Activity ac;
    private Data app;

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
        userNickname = (TextView)view.findViewById(R.id.user_nickname);
        userSummary = (TextView)view.findViewById(R.id.user_summary);
        userName = (TextView)view.findViewById(R.id.user_name);
        requestQueue = Volley.newRequestQueue(getActivity());

        app = (Data)getActivity().getApplication();
        setText();

        return view;
    }
    void setText(){
        String nickname = app.getNickname();
        String summary = app.getSummary();
        String username = app.getUsername();
        userNickname.setText(nickname);
        userSummary.setText(summary);
        userName.setText(username);
    }

    @Override
    public void onStart() {
        super.onStart();
        user_info = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        userId = user_info.getInt("uid", -1);

        HashMap<String, Integer> userMap = new HashMap<String, Integer>();
        userMap.put("userId", userId);
        JSONObject map = new JSONObject(userMap);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int userId = response.getInt("userId");
                    if (userId >= 0) {
                        String nickname = response.getString("nickname");
                        String summary = response.getString("summary");
                        if (nickname.equals("null")) {
                            nickname = "";
                        }
                        if (summary.equals("null")) {
                            summary = "";
                        }
                        app.setNickname(nickname);
                        app.setSummary(summary);
                        userNickname.setText(nickname);
                        userSummary.setText(summary);
                    }
                } catch (JSONException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.getMessage(), error);
            }
        });
        requestQueue.add(req);
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
                bundle.putString("nickname", userNickname.getText().toString());
                bundle.putString("summary", userSummary.getText().toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), UpdateActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
