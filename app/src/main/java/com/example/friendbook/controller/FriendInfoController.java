package com.example.friendbook.controller;

import android.content.Intent;
import android.view.View;

import cn.jpush.im.android.api.model.UserInfo;
import com.example.friendbook.R;
import com.example.friendbook.activity.FriendInfoActivity;
//import com.example.friendbook.activity.FriendSettingActivity;
import com.example.friendbook.view.FriendInfoView;

/**
 * Created by ${chenyn} on 2017/3/24.
 */

public class FriendInfoController implements View.OnClickListener {
    private FriendInfoActivity mContext;
    private UserInfo friendInfo;

    public FriendInfoController(FriendInfoView friendInfoView, FriendInfoActivity context) {
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goToChat:
                mContext.startChatActivity();
                break;
            case R.id.return_btn:
                mContext.finish();
                break;
            default:
                break;
        }
    }

    public void setFriendInfo(UserInfo info) {
        friendInfo = info;
    }

}
