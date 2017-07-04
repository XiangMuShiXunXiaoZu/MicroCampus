package com.android.app.microcampus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.android.app.microcampus.R.id.action_release;

public class ItemActivity extends AppCompatActivity {

    private String mitemName;
    private String mdescription;
    private EditText itemName;
    private EditText itemDescription;
    private Bitmap bitmap;
    private ImageButton imgButtom;
    VectorDrawableCompat vectorDrawableColored, vectorDrawable;

    private static RequestQueue requestQueue;
    private static int PICK_IMAGE_REQUEST = 1;
    private static final String url="http://123.206.125.253/additem"; //所需url
    private int userId;
    private double latitude;
    private double longitude;
    private boolean hasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        requestQueue = Volley.newRequestQueue(this);
        Data app = (Data)getApplication();
        userId = app.getUserId();
        latitude = app.getLatitude();
        longitude = app.getLongitude();


        vectorDrawableColored = VectorDrawableCompat.create(getResources(),R.drawable.ic_photo_library_grey_24dp,getTheme());
        vectorDrawableColored.setTint(getResources().getColor(R.color.colorAccent));
        vectorDrawable = VectorDrawableCompat.create(getResources(),R.drawable.ic_photo_library_grey_24dp,getTheme());
        vectorDrawable.setTint(getResources().getColor(R.color.jumbo));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgButtom = (ImageButton)findViewById(R.id.add_photo);
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
            hasImage = true;
            imgButtom.setImageDrawable(vectorDrawableColored);
            Uri filepath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);

            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            hasImage = false;
            imgButtom.setImageDrawable(vectorDrawable);
        }
    }

    private void addItem() {
        mitemName = itemName.getText().toString().trim();
        mdescription = itemDescription.getText().toString().trim();

        if (mitemName.isEmpty()) {
            itemName.setError("请输入物品名称");
            return;
        } else {
            itemName.setError(null);
        }

        if (mdescription.isEmpty()) {
            Toast.makeText(getBaseContext(), "请添加描述", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasImage) {
            Toast.makeText(getBaseContext(), "请添加图片", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(ItemActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("上传中...");
        progressDialog.show();

        HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
        itemMap.put("userId",userId);
        itemMap.put("itemName", mitemName);
        itemMap.put("description", mdescription);
        itemMap.put("latitude",latitude);
        itemMap.put("longitude",longitude);
        String image = getStringImage(bitmap);
        itemMap.put("image", image);
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
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                    onAddItemFailed();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                progressDialog.dismiss();
                Log.e("ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败，图片大小可能超出限制", Toast.LENGTH_SHORT).show();
                onAddItemFailed();
            }
        });

        requestQueue.add(req);
    }

    private void onAddItemFailed() {
    }

    private void onAddItemSuccess() {
        Toast.makeText(getBaseContext(), "添加成功！", Toast.LENGTH_SHORT).show();
        finish();
    }

}
