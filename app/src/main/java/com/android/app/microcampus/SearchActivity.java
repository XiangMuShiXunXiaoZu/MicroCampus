package com.android.app.microcampus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.app.microcampus.R.id.imageView;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView mySearchView;
    private static final String searchUrl="http://123.206.125.253/searchitem"; //所需url
    private static final String imageUrl="http://123.206.125.253/getimage"; //所需url
    private ArrayList<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
    private SimpleAdapter myAdapter;
    private boolean isSearching = false;
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                        //用toolbar替换原来的ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
        ListView listView = (ListView)findViewById(R.id.listView);

        requestQueue = Volley.newRequestQueue(this);
        myAdapter = new SimpleAdapter(this,
                listems,
                R.layout.list_item,
                new String[]{"itemName", "itemSummary","image"},
                new int[]{R.id.titleTextView, R.id.descTextView, R.id.imageView});
        listView.setAdapter(myAdapter);
        myAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            @Override
            public boolean setViewValue(View view, Object bitmapData, String s) {
                if(view instanceof ImageView && bitmapData instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) bitmapData);
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                Bundle bundle = new Bundle();
                bundle.putInt("itemId", Integer.valueOf(listems.get(arg2).get("itemId").toString()));
                bundle.putInt("userId", Integer.valueOf(listems.get(arg2).get("userId").toString()));
                bundle.putString("itemName", (listems.get(arg2).get("itemName").toString()));
                bundle.putString("itemSummary", (listems.get(arg2).get("itemSummary").toString()));
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(SearchActivity.this, ViewItemActivity.class);
                startActivity(intent);
            }
        });
        //TODO finish the logic
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);//指定Toolbar上的视图文件
        mySearchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        mySearchView.onActionViewExpanded();
        mySearchView.setQueryHint("查找物品");
        mySearchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        if(!isSearching) {
            requestQueue.cancelAll(this.getClass().getSimpleName());//清空请求
            searchItem();
        }
        mySearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchItem() {
        isSearching = true;
        String mSearchViewText = mySearchView.getQuery().toString().trim();
        HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
        itemMap.put("itemName", mSearchViewText);
        JSONObject map=new JSONObject(itemMap);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,searchUrl,map,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isSearching = false;
                listems.clear();
                try{
                    showItem(response);
                    myAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                isSearching = false;
                listems.clear();
                Log.e("ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(req);
    }

    private void showItem(JSONObject result) throws JSONException{
        int len = result.getInt("itemNumber");
        for(int i = 0; i < len; i++){
            final int index = i;
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("userId", result.getString("userId"+i));
            listem.put("itemId", result.getString("itemId"+i));
            listem.put("itemName", result.getString("itemName"+i));
            listem.put("itemSummary", result.getString("itemSummary"+i));
            listem.put("image", null);
            ImageRequest imageRequest = new ImageRequest(imageUrl+"?uid="+result.getInt("userId"+i)+"&iid="+result.getInt("itemId"+i),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            Map<String, Object> listem = listems.get(index);
                            listem.put("image",response);
                            listems.set(index, listem);
                            myAdapter.notifyDataSetChanged();
                        }
                        //0，0表示最大宽和高 RGB_565是颜色模式
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(imageRequest);
            listems.add(listem);
        }
    }
}
