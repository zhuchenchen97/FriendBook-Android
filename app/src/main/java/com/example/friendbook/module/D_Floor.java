package com.example.friendbook.module;

import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 12410 on 2017/11/3.
 */

public class D_Floor {
    //private String floor_name;
    private int floor_number;
    private String floor_time;
    private String floor_content;
    //private int floor_headId;
    private UserInfo userInfo;
    public D_Floor(UserInfo userInfo,int floor_number,String floor_time,String floor_content){
        //this.floor_name = floor_name;
        this.userInfo = userInfo;
        this.floor_number = floor_number;
        this.floor_time = floor_time;
        this.floor_content = floor_content;
        //this.floor_headId = floor_headId;
    }
    /*
    public void set_Floor_Name(String floor_name){
        this.floor_name = floor_name;
    }
    public void set_Floor_HeadID(int floor_headId){
        this.floor_headId = floor_headId;
    }
    public int get_Floor_HeadId(){
        return floor_headId;
    }
    public String get_Floor_Name(){
        return floor_name;
    }
    */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void set_Floor_Number(int floor_number){
        this.floor_number = floor_number;
    }
    public void set_Floor_Time(String floor_time){
        this.floor_time = floor_time;
    }
    public void set_Floor_Content(String floor_content){
        this.floor_content = floor_content;
    }
    public int get_Floor_Number(){
        return floor_number;
    }
    public String get_Floor_Time(){
        return floor_time;
    }
    public String get_Floor_Content(){
        return floor_content;
    }

}
