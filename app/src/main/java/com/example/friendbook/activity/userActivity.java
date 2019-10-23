package com.example.friendbook.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.friendbook.R;

import cn.jpush.im.android.api.JMessageClient;

public class userActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void changepwd(View v){
        Intent intent = new Intent(userActivity.this,changepwdActivity.class);
        startActivity(intent);
    }

    public void exit(View v){
        AVUser.logOut();// 清除缓存用户对象
        JMessageClient.logout();
        AVUser currentUser = AVUser.getCurrentUser();// 现在的 currentUser 是 null 了
        if (currentUser == null) {

            Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(userActivity.this,LoginActivity.class));
        }
    }
}
