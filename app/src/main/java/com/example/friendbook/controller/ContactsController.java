package com.example.friendbook.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import com.example.friendbook.R;
import com.example.friendbook.activity.FriendRecommendActivity;
import com.example.friendbook.activity.SearchContactsActivity;
import com.example.friendbook.adapter.StickyListAdapter;
import com.example.friendbook.database.FriendEntry;
import com.example.friendbook.database.UserEntry;
import com.example.friendbook.Util.pinyin.HanziToPinyin;
import com.example.friendbook.Util.pinyin.PinyinComparator;
import com.example.friendbook.Util.SideBar;
import com.example.friendbook.view.ContactsView;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ContactsController implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener {
    private ContactsView mContactsView;
    private Activity mContext;
    private List<FriendEntry> mList = new ArrayList<>();
    private StickyListAdapter mAdapter;


    public ContactsController(ContactsView mContactsView, FragmentActivity context) {
        this.mContactsView = mContactsView;
        this.mContext = context;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.verify_ll://验证消息
                intent.setClass(mContext, FriendRecommendActivity.class);
                mContext.startActivity(intent);
                mContactsView.dismissNewFriends();
                break;
            default:
                break;
        }
    }


    public void initContacts() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mContactsView.showLoadingHeader();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                if (responseCode == 0) {
                    if (userInfoList.size() != 0) {
                        mContactsView.dismissLine();
                        ActiveAndroid.beginTransaction();
                        try {
                            for (UserInfo userInfo : userInfoList) {
                                String displayName = userInfo.getNotename();
                                if (TextUtils.isEmpty(displayName)) {
                                    displayName = userInfo.getNickname();
                                    if (TextUtils.isEmpty(displayName)) {
                                        displayName = userInfo.getUserName();
                                    }
                                }
                                String letter;
                                ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                                        .get(displayName);
                                StringBuilder sb = new StringBuilder();
                                if (tokens != null && tokens.size() > 0) {
                                    for (HanziToPinyin.Token token : tokens) {
                                        if (token.type == HanziToPinyin.Token.PINYIN) {
                                            sb.append(token.target);
                                        } else {
                                            sb.append(token.source);
                                        }
                                    }
                                }
                                String sortString = sb.toString().substring(0, 1).toUpperCase();
                                if (sortString.matches("[A-Z]")) {
                                    letter = sortString.toUpperCase();
                                } else {
                                    letter = "#";
                                }
                                //避免重复请求时导致数据重复
                                FriendEntry friend = FriendEntry.getFriend(user,
                                        userInfo.getUserName(), userInfo.getAppKey());
                                if (null == friend) {
                                    if (TextUtils.isEmpty(userInfo.getAvatar())) {
                                        friend = new FriendEntry(userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
                                                null, displayName, letter, user);
                                    } else {
                                        friend = new FriendEntry(userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
                                                userInfo.getAvatarFile().getAbsolutePath(), displayName, letter, user);
                                    }
                                    friend.save();
                                    mList.add(friend);
                                }

                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                    } else {
                        mContactsView.showLine();
                    }
                    mContactsView.dismissLoadingHeader();
                    Collections.sort(mList, new PinyinComparator());
                    mAdapter = new StickyListAdapter(mContext, mList, true);
                    mContactsView.setAdapter(mAdapter);
                } else {
                    mContactsView.dismissLoadingHeader();
                }
            }
        });

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        if (null != mAdapter) {
            int position = mAdapter.getSectionForLetter(s);
            if (position != -1 && position < mAdapter.getCount()) {
                mContactsView.setSelection(position);
            }
        }
    }

    public void refresh(FriendEntry entry) {
        mList.add(entry);
        if (null == mAdapter) {
            mAdapter = new StickyListAdapter(mContext, mList, true);
        } else {
            Collections.sort(mList, new PinyinComparator());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void refreshContact() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mList = user.getFriends();
        Collections.sort(mList, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mList, true);
        mContactsView.setAdapter(mAdapter);
    }

}
