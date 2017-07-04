package com.android.app.microcampus;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    Fragment homepage;
    Fragment msgNotification;
    Fragment person;
    Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data app = (Data)getApplication();
        SharedPreferences user_info = getSharedPreferences("user_info", MODE_PRIVATE);
        if (user_info.getInt("uid", -1) < 0){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            app.setUserId(user_info.getInt("uid", -1));
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        homepage = new HomePage();
        msgNotification = new MsgNotification();
        person = new Person();
        currentFragment = homepage;

        FragmentManager fm = getSupportFragmentManager();
        if (!homepage.isAdded()) {    // 先判断是否被add过
            fm.beginTransaction().add(R.id.content, homepage).commit();
        } else {
            fm.beginTransaction().show(homepage).commit();
        }

        getSupportActionBar().setTitle("微校园");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentHome();
                    currentFragment = homepage;
                    getSupportActionBar().setTitle("微校园");
                    return true;
                case R.id.navigation_message:
                    switchToFragmentNews();
                    currentFragment = msgNotification;
                    getSupportActionBar().setTitle("消息与通知");
                    return true;
                case R.id.navigation_person:
                    switchToFragmentPerson();
                    currentFragment = person;
                    getSupportActionBar().setTitle("个人信息");
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        homepage.onResume();
        msgNotification.onResume();
        person.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        homepage.onPause();
        msgNotification.onPause();
        person.onPause();
    }

    public void switchContent(Fragment from, Fragment to) {
        FragmentManager fm = getSupportFragmentManager();
        if (!to.isAdded()) {    // 先判断是否被add过
            fm.beginTransaction().hide(from).add(R.id.content, to).commit();
        } else {
            fm.beginTransaction().hide(from).show(to).commit();
        }
    }
    public void switchToFragmentHome(){
        switchContent(currentFragment, homepage);
    }

    public void switchToFragmentNews(){
        switchContent(currentFragment, msgNotification);
    }

    public void switchToFragmentPerson(){
        switchContent(currentFragment, person);
    }


}
