package com.android.app.microcampus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                        //用toolbar替换原来的ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);//指定Toolbar上的视图文件
        SearchView mySearchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        mySearchView.onActionViewExpanded();
        mySearchView.setSubmitButtonEnabled(true);
        mySearchView.setQueryHint("查找物品");
        return super.onCreateOptionsMenu(menu);
    }
}
