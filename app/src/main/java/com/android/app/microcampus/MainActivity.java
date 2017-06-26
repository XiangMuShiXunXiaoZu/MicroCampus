package com.android.app.microcampus;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToFragmentHome();
        getSupportActionBar().setTitle("微校园");
    }

    @Override
    public void onResume(){
        super.onResume();
        user_info = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = user_info.getString("username", "");
        String password = user_info.getString("password", "");
        int userId = user_info.getInt("uid", -1);
        if (username.isEmpty() || password.isEmpty() || userId < 0) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentHome();
                    getSupportActionBar().setTitle("微校园");
                    return true;
                case R.id.navigation_message:
                    switchToFragmentNews();
                    getSupportActionBar().setTitle("消息与通知");
                    return true;
                case R.id.navigation_person:
                    switchToFragmentPerson();
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

    public void switchToFragmentHome(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content,new HomePage()).commit();
    }

    public void switchToFragmentNews(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content,new MsgNotification()).commit();
    }

    public void switchToFragmentPerson(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content,new Person()).commit();
    }


}
