package com.example.friendbook.activity;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.friendbook.R;
import com.example.friendbook.Util.NativeImageLoader;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

public class BigPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_picture);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//设置状态栏颜色
        window.setStatusBarColor(Color.BLACK);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
        PhotoView photoView = (PhotoView)findViewById(R.id.big_photo_view);
        String path = getIntent().getStringExtra("avatarPath");
        photoView.setImageBitmap(NativeImageLoader.getInstance().getBitmapFromMemCache(path));
    }
}
