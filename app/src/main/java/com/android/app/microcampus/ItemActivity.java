package com.android.app.microcampus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.android.app.microcampus.R.id.action_release;

public class ItemActivity extends AppCompatActivity {

    private String mitemName;
    private String mdescription;
    private EditText itemName;
    private EditText itemDescription;
    private Bitmap bitmap;

    private static RequestQueue requestQueue;
    private static int PICK_IMAGE_REQUEST = 1;
    private static final String url="http://123.206.125.253/additem"; //所需url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        requestQueue = Volley.newRequestQueue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemName = (EditText)findViewById(R.id.itemName);
        itemDescription = (EditText)findViewById(R.id.itemDescription);
        ImageButton imageButton = (ImageButton)findViewById(R.id.add_photo);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoChoose();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case action_release:
                addItem();
                return true;
            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPhotoChoose(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),PICK_IMAGE_REQUEST);
    }

    //将图片从Bitmap转换为String类型
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filepath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void addItem() {
        mitemName = itemName.getText().toString().trim();
        mdescription = itemDescription.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(ItemActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("上传中...");
        progressDialog.show();

        HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
        itemMap.put("itemName", mitemName);
        itemMap.put("description", mdescription);
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                Map<String,String> map1 = new HashMap<String,String>();

                map1.put("image",image);
                return map1;
            }
        };


        requestQueue.add(req);
        requestQueue.add(stringRequest);
    }

    private void onAddItemFailed() {
    }

    private void onAddItemSuccess() {
        finish();
    }

}
