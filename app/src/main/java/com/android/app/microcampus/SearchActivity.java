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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {


    private ListView listView;
    private BaseAdapter adapter;
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
//        adapter = new SimpleCursorAdapter()；
//
//        listView = (ListView)findViewById(R.id.listView);
//        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);//指定Toolbar上的视图文件
        mySearchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        mySearchView.onActionViewExpanded();
        mySearchView.setSubmitButtonEnabled(true);
        mySearchView.setQueryHint("查找物品");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.ab_search:
                searchItem();
                return true;
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
                Toast.makeText(getBaseContext(), "连接服务器失败", Toast.LENGTH_LONG).show();
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
