package com.example.friendbook.Util.pinyin;


import android.text.TextUtils;

import java.util.Comparator;

import cn.jpush.im.android.api.model.UserInfo;

public class UserComparator implements Comparator<UserInfo> {

    public int compare(UserInfo o1, UserInfo o2) {
        String notename = o1.getNotename();
        if (TextUtils.isEmpty(notename)){
            notename = o1.getNickname();
            if (TextUtils.isEmpty(notename)) {
                notename = o1.getUserName();
            }
        }

        String notename2 = o2.getNotename();
        if (TextUtils.isEmpty(notename2)){
            notename2 = o2.getNickname();
            if (TextUtils.isEmpty(notename2)) {
                notename2 = o2.getUserName();
            }
        }

        //要转成拼音否则有中文有英文时候比较出来的结果不正确
        notename = HanyuPinyin.getInstance().getStringPinYin(notename.substring(0,1));
        notename2 = HanyuPinyin.getInstance().getStringPinYin(notename2.substring(0,1));

        return notename.compareTo(notename2);
    }



}
