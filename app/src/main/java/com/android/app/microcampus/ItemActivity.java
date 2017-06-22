package com.android.app.microcampus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.EditText;

public class ItemActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        EditText itemName = (EditText)findViewById(R.id.itemName);
        EditText itemDescription = (EditText)findViewById(R.id.itemName);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.itemmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
