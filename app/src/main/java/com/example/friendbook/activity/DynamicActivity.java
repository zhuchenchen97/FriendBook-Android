package com.example.friendbook.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.friendbook.R;
import com.example.friendbook.Util.TimeFormat;
import com.example.friendbook.adapter.D_FloorAdapter;
import com.example.friendbook.module.D_Floor;
import com.example.friendbook.module.Dynamic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;


public class DynamicActivity extends BaseActivity implements View.OnClickListener{
    private EditText editText;
    private List<D_Floor> floorList = new ArrayList<>();
    private String objectId = "";
    private AVUser avUser;
    private DynamicActivity dynamicActivity;
    private Dynamic dynamic;
    private boolean isChecked = false;
    private String praise = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dynamicActivity = this;
        setContentView(R.layout.activity_dynamic);
        getSupportActionBar().hide();
        editText = (EditText)findViewById(R.id.comment_content);
        Button button = (Button) findViewById(R.id.comment_launch);
        initTitle(true,true,"详情","",false,"");
        button.setOnClickListener(this);
        objectId = getIntent().getStringExtra("dynamic_id");
        dynamic = (Dynamic)getIntent().getSerializableExtra("dynamic");
        isChecked = getIntent().getBooleanExtra("ischeck",false);
        praise = getIntent().getStringExtra("praise");
        //initfloors();
        initfloors2();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comment_launch: //   　　点击发布文本
                System.out.println("2");
                String edit_text = editText.getText().toString();
                if(edit_text.isEmpty() )
                    Toast.makeText(DynamicActivity.this,"发布内容不能为空",Toast.LENGTH_SHORT).show();
                else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DynamicActivity.this);
                    dialog.setTitle("发布评论");
                    dialog.setMessage("您是否确认发布该评论");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dialog.setNegativeButton("发布", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String edit_text11 = editText.getText().toString();
                            SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
                            Date curDate =  new Date(System.currentTimeMillis());
                            String   str   =   formatter.format(curDate);
                            Toast.makeText(DynamicActivity.this,edit_text11+str,Toast.LENGTH_SHORT).show();// 待修改
                            editText.setText("");
                            final AVObject dfloor = new AVObject("DFloor");
                            dfloor.put("content",edit_text11);
                            dfloor.put("user", AVUser.getCurrentUser());
                            AVObject dynamicObject = AVObject.createWithoutData("Dynamic",objectId);
                            dfloor.put("dynamic", dynamicObject);
                            AVQuery<AVObject> query = new AVQuery<>("DFloor");
                            query.whereEqualTo("dynamic", dynamicObject);
                            dfloor.put("answerFloor", 1);
                            query.countInBackground(new CountCallback() {
                                @Override
                                public void done(int i, AVException e) {
                                    if (e == null) {
                                        dfloor.put("floorNumber",i+1);
                                        // 查询成功，输出计数
                                        dfloor.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if(e== null)
                                                {
                                                    floorList.clear();
                                                    initfloors2();
                                                }

                                                else
                                                    Toast.makeText(DynamicActivity.this,"失败",Toast.LENGTH_SHORT).show();
                                            }

                                        });
                                    } else {
                                        // 查询失败
                                    }
                                }
                            });


                        }
                    });
                    dialog.show();
                    //finish();
                }
                break;

            default:
                break;
        }
    }
    /*
    private void initfloors(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.dynamic_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(int i= 0;i<10;i++)
        {
            int j = i+1;
            SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
            Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间
            String    str    =    formatter.format(curDate);
            D_Floor d_floor = new D_Floor("王强",j,str,"大家晚上好，hahaha",R.drawable.test_picture);
            floorList.add(d_floor);
            D_Floor d_floor2 = new D_Floor("王2强",j,str,"大家晚2上好，hah2aha",R.drawable.test_picture);
            floorList.add(d_floor2);
        }
        D_FloorAdapter d_floorAdapter = new D_FloorAdapter(floorList,this);
        recyclerView.setAdapter(d_floorAdapter);
        //d_floorAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.item_footer_layout,null));
        d_floorAdapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.share_list_item,null));//R.layout.item_header_layout  R.layout.share_list_item
    }*/
    private void initfloors2(){
        Log.e("DynamicActivity","开始刷新");
        AVQuery<AVObject> dFloors = new AVQuery<>("DFloor");
        dFloors.whereEqualTo("dynamic",AVObject.createWithoutData("Dynamic", objectId));
        dFloors.include("user");
        dFloors.include("dynamic");
        dFloors.orderByAscending("floorNumber");
        dFloors.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (e == null){
                    Log.e("DynamicList","获得数据");
                    Log.e("DynamicList","获得数据"+list.size());
                    Log.e("DynamicList","获得数据"+floorList.size());
                    if (list.size() == 0){
                        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.dynamic_recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(dynamicActivity));
                        D_FloorAdapter d_floorAdapter = new D_FloorAdapter(floorList,dynamicActivity);
                        d_floorAdapter.addHeaderView(LayoutInflater.from(dynamicActivity).inflate(R.layout.share_list_item,null),dynamic,praise,isChecked);
                        recyclerView.setAdapter(d_floorAdapter);
                    }else {
                        for (AVObject dFloor:list){
                            final String content = dFloor.getString("content");
                            final Number floorNumber = dFloor.getNumber("floorNumber");
                            String numberString = floorNumber.toString();
                            final int number = Integer.parseInt(numberString);
                            final Date launchDate = dFloor.getDate("createdAt");
                            final String date = new TimeFormat(DynamicActivity.this,launchDate.getTime()).getTime();
                            final Number answerNumber = dFloor.getNumber("answerFloor");
                            //final String userId = dFloor.getString("user");
                            avUser = dFloor.getAVUser("user");
                            final String userId = avUser.getUsername();
                            JMessageClient.getUserInfo(userId, new GetUserInfoCallback() {
                                @Override
                                public void gotResult(int i, String s, UserInfo userInfo) {
                                    if (i == 0){
                                        Log.e("hh",content+floorNumber+launchDate+answerNumber+userId);
                                        D_Floor d_floor = new D_Floor(userInfo,number,date,content);
                                        floorList.add(d_floor);
                                        if (floorList.size()==list.size()){
                                            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.dynamic_recyclerView);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(dynamicActivity));
                                            D_FloorAdapter d_floorAdapter = new D_FloorAdapter(floorList,dynamicActivity);
                                            d_floorAdapter.addHeaderView(LayoutInflater.from(dynamicActivity).inflate(R.layout.share_list_item,null),dynamic,praise,isChecked);
                                            recyclerView.setAdapter(d_floorAdapter);
                                            //d_floorAdapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.item_footer_layout,null));

                                        }
                                    }else {
                                        Toast.makeText(DynamicActivity.this,"获取数据出错",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }

                }else {
                    Log.e("DynamicActivity",e.getMessage());
                }
            }
        });

    }

}
