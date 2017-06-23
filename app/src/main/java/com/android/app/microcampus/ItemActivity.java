package com.android.app.microcampus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import static com.android.app.microcampus.R.id.action_add_photo;

public class ItemActivity extends AppCompatActivity {



    private String itemName;
    private String description;
    private static RequestQueue requestQueue;
    private static final String url="http://123.206.125.253/additem"; //所需url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        requestQueue = Volley.newRequestQueue(this);


        EditText mitemName = (EditText)findViewById(R.id.itemName);
        EditText mitemDescription = (EditText)findViewById(R.id.itemName);

        itemName = mitemName.getText().toString();
        description = mitemDescription.getText().toString();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itemmenu,menu);
        return super.onCreateOptionsMenu(menu);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case action_add_photo:
                addPhoto();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPhoto() {

        final ProgressDialog progressDialog = new ProgressDialog(ItemActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("上传中...");
        progressDialog.show();

        HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
        itemMap.put("itemName", itemName);
        itemMap.put("description", description);
        JSONObject map=new JSONObject(itemMap);

        //TODO finish the logic

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,map,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try{
                    int itemId = response.getInt("itemId");
                    if (itemId >= 0){

                        onAddItemSuccess();
                    }else {
                        onAddItemFailed();
                        Toast.makeText(getBaseContext(), "物品添加失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("LOGIN-ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                    onAddItemFailed();
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                progressDialog.dismiss();
                Log.e("LOGIN-ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
                onAddItemFailed();
            }
        });
        requestQueue.add(req);
    }

    private void onAddItemFailed() {
    }

    private void onAddItemSuccess() {
        finish();
    }

}
