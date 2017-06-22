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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String url="http://123.206.125.253/login"; //所需url
    private static RequestQueue requestQueue;
    private SharedPreferences user_info;
    private String username = "", password = "";

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Log.v("INFO","default result.");
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        user_info = getSharedPreferences("user_info", MODE_PRIVATE);
        username = user_info.getString("username", "");
        password = user_info.getString("password", "");
        _usernameText.setText(username);
        _passwordText.setText(password);
        if (!username.isEmpty() && !password.isEmpty()) login();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        HashMap<Object, Object>userMap=new HashMap<Object, Object>();
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
                        onLoginSuccess();
                    }else {
                        onLoginFailed();
                        Toast.makeText(getBaseContext(), "用户名或密码有误", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("LOGIN-ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_LONG).show();
                    onLoginFailed();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                progressDialog.dismiss();
                Log.e("LOGIN-ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
                onLoginFailed();
            }
        });
        requestQueue.add(req);

    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        username = _usernameText.getText().toString();
        password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 6 || username.length() > 20) {
            _usernameText.setError("输入有效的用户名");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            _passwordText.setError("长度应该在6～20个字符之间");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}
