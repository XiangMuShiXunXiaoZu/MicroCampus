package com.android.app.microcampus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    private ListView listView;
    private SearchView mySearchView;
    private static final String url="http://123.206.125.253/searchitem"; //所需url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                        //用toolbar替换原来的ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标

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
        searchItem();
        ArrayList<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        Map<String, Object> listem = new HashMap<String, Object>();
        for(int i=0;i<10;i++){
            listem.put("title", "手机");
            listem.put("description", "过年亲友送的iPhone8 Plus，全新，自己用不上。希望低价出手给识货的同学，200不包邮。所有发票齐全，可小刀可面交。");
            listems.add(listem);
        }
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new SimpleAdapter(this,
                listems,
                R.layout.list_item,
                new String[]{"title", "description"},
                new int[]{R.id.titleTextView, R.id.descTextView}));
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
        String mSearchViewText = mySearchView.getQuery().toString().trim();
        HashMap<Object, Object> itemMap=new HashMap<Object, Object>();
        itemMap.put("searchText", mSearchViewText);
        JSONObject map=new JSONObject(itemMap);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,url,map,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    int itemId = response.getInt("itemId");
                    if (itemId >= 0){
                        showItem();
                    }else {
                        noFindItem();
                        Toast.makeText(getBaseContext(), "找不到对应物品", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("LOGIN-ERROR", e.getMessage(), e);
                    Toast.makeText(getBaseContext(), "服务器返回参数有误", Toast.LENGTH_SHORT).show();
                    onSearchItemFailed();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("LOGIN-ERROR", error.getMessage(), error);
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                onSearchItemFailed();
            }
        });
    }

    private void onSearchItemFailed() {

    }

    private void showItem() {
    }

    private void noFindItem() {
    }
}
