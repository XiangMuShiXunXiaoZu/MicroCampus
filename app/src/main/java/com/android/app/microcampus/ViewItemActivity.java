package com.android.app.microcampus;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class ViewItemActivity extends AppCompatActivity {
    private static final String imageUrl="http://123.206.125.253/getlargeimage"; //所需url
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        Bundle bundle = getIntent().getExtras();
        TextView itemTitle = (TextView)findViewById(R.id.itemTitleView);
        TextView itemDesc = (TextView)findViewById(R.id.itemDescView);
        TextView distance = (TextView)findViewById(R.id.itemLocationView);
        distance.setText(bundle.getString("distance"));
        itemTitle.setText(bundle.getString("itemName"));
        itemDesc.setText(bundle.getString("itemSummary"));
        final ImageView itemImage = (ImageView)findViewById(R.id.itemImageView);
        requestQueue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(imageUrl+"?uid="+bundle.getInt("userId")+"&iid="+bundle.getInt("itemId"),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        itemImage.setImageBitmap(response);
                    }
                    //0，0表示最大宽和高 RGB_565是颜色模式
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(imageRequest);
    }
}
