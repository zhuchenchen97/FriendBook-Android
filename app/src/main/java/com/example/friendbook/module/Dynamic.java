package com.example.friendbook.module;

import android.content.Context;
import android.util.Log;

import com.example.friendbook.Util.TimeFormat;
import com.example.friendbook.Util.Util;
import com.lzy.ninegrid.ImageInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 11972 on 2017/10/6.
 */

public class Dynamic implements Serializable{
    public static int count = 3 ;
    private String dynamic_id;//leancloud中标识动态的id
    private String dynamic_short_content;//当动态的文本内容超过5行时，超过5行的不显示。
    private String dynamic_time;//动态发布的时间
    private List<String> dynamic_images;//动态包含的图片
    private String dynamic_content;//动态的具体文本内容
    private String creator_userName;//动态的发布者的相关信息
    private List<String> praiseUserNames;//对该条动态点赞的用户的信息

    public Dynamic(Context context,String dynamic_id, String dynamic_content, String dynamic_short_content, Date dynamic_createAt, String creator_userName, List<String> imagesUrl, List<String> praiseUserNames) {
        this.dynamic_id = dynamic_id;
        this.dynamic_content = dynamic_content;
        this.dynamic_short_content = dynamic_short_content;
        this.dynamic_time = new TimeFormat(context,dynamic_createAt.getTime()).getTime();
        this.creator_userName =  creator_userName;
        this.dynamic_images = imagesUrl;
        this.praiseUserNames = praiseUserNames;
    }

    public String getDynamic_id() {
        return dynamic_id;
    }

    public void setDynamic_id(String dynamic_id) {
        this.dynamic_id = dynamic_id;
    }

    public String getDynamic_short_content() {
        return dynamic_short_content;
    }

    public void setDynamic_short_content(String dynamic_short_content) {
        this.dynamic_short_content = dynamic_short_content;
    }

    public String getDynamic_time() {
        return dynamic_time;
    }

    public void setDynamic_time(String dynamic_time) {
        this.dynamic_time = dynamic_time;
    }

    public List<String> getDynamic_images() {
        return dynamic_images;
    }

    public void setDynamic_images(List<String> dynamic_images) {
        this.dynamic_images = dynamic_images;
    }

    public String getDynamic_content() {
        return dynamic_content;
    }

    public void setDynamic_content(String dynamic_content) {
        this.dynamic_content = dynamic_content;
    }

    public String getUserInfo() {
        return creator_userName;
    }

    public void setUserInfo(String creator_userName) {
        this.creator_userName = creator_userName;
    }

    public List<String> getPraiseUserInfos() {
        return praiseUserNames;
    }

    public void setPraiseUserInfos(List<String> praiseUserNames) {
        this.praiseUserNames = praiseUserNames;
    }
}
