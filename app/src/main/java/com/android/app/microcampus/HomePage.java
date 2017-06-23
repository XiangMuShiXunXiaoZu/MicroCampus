package com.android.app.microcampus;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static com.android.app.microcampus.R.id.action_search;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {

    private FloatingActionButton fab;

    public HomePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page,container,false);
        setHasOptionsMenu(true);


        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ItemActivity.class);
                startActivity(intent);
            }
        });

//        Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home_page, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
