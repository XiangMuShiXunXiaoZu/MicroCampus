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

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String url="http://123.206.125.253/signup"; //所需url
    private static RequestQueue requestQueue;
    private SharedPreferences user_info;
    private String username = "", password = "";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("注册中...");
        progressDialog.show();

        HashMap<Object, Object> userMap=new HashMap<Object, Object>();
        userMap.put("username", username);
        userMap.put("password", password);
        JSONObject map=new JSONObject(userMap);

        JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST,url,map,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                progressDialog.dismiss();
                try{
                    int userId = response.getInt("userId");
                    if (userId >= 0){
                        user_info = getSharedPreferences("user_info", MODE_PRIVATE);
                        SharedPreferences.Editor edt = user_info.edit();
                        edt.putString("username", username);
                        edt.putString("password", password);
                        edt.putInt("uid", userId);
                        edt.commit();
                        onSignupSuccess();
                    }else {
                        onSignupFailed();
                        Toast.makeText(getBaseContext(), "用户名已被占用", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("LOGIN-ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_LONG).show();
                    onSignupFailed();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                progressDialog.dismiss();
                Log.e("LOGIN-ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
                onSignupFailed();
            }
        });
        requestQueue.add(req);

    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        username = _nameText.getText().toString();
        password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (username.isEmpty() || username.length() < 6 || username.length() > 20) {
            _nameText.setError("长度应该在6～10个字符之间");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            _passwordText.setError("长度应该在6～10个字符之间");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("密码不一致");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}

