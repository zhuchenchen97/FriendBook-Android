package com.example.friendbook.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.friendbook.Application.JGApplication;
import com.example.friendbook.R;
import com.example.friendbook.Util.SharePreferenceManager;
import com.example.friendbook.Util.ThreadUtil;
import com.example.friendbook.Util.pinyin.HanziToPinyin;
import com.example.friendbook.controller.ContactsController;
import com.example.friendbook.database.FriendEntry;
import com.example.friendbook.database.FriendRecommendEntry;
import com.example.friendbook.database.UserEntry;
import com.example.friendbook.entity.Event;
import com.example.friendbook.entity.EventType;
import com.example.friendbook.entity.FriendInvitation;
import com.example.friendbook.fragment.ContactsFragment;
import com.example.friendbook.view.ContactsView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;

import cn.jpush.im.android.api.model.UserInfo;

public class FriendListActivity extends FragmentActivity {
    private ContactsView mContactsView;
    private ContactsController mContactsController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        setContentView(R.layout.activity_friend_list);
        FragmentManager fm =getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.friend_list_view);
        if (fragment == null){
            fragment = new ContactsFragment();
            fm.beginTransaction().add(R.id.friend_list_view,fragment).commit();
        }

    }

    //接收到好友事件
    public void onEvent(ContactNotifyEvent event) {
        Log.e("FriendListActivity","收到好友申请事件");
        final UserEntry user = JGApplication.getUserEntry();
        final String reason = event.getReason();
        final String username = event.getFromUsername();
        final String appKey = event.getfromUserAppKey();
        //对方接收了你的好友请求
        if (event.getType() == ContactNotifyEvent.Type.invite_accepted) {
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    if (responseCode == 0) {
                        String name = info.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = info.getUserName();
                        }
                        FriendEntry friendEntry = FriendEntry.getFriend(user, username, appKey);
                        if (friendEntry == null) {
                            final FriendEntry newFriend = new FriendEntry(username, info.getNotename(), info.getNickname(), appKey, info.getAvatar(), name, getLetter(name), user);
                            newFriend.save();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContactsController.refresh(newFriend);
                                }
                            });
                        }
                    }
                }
            });
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            entry.state = FriendInvitation.ACCEPTED.getValue();
            entry.save();
            //拒绝好友请求
        } else if (event.getType() == ContactNotifyEvent.Type.invite_declined) {
            FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
            entry.state = FriendInvitation.BE_REFUSED.getValue();
            entry.reason = reason;
            entry.save();
            //收到好友邀请
        } else if (event.getType() == ContactNotifyEvent.Type.invite_received) {
            //Log.e("ContactsFragment","收到好友申请事件");
            JMessageClient.getUserInfo(username, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String desc, UserInfo userInfo) {
                    if (status == 0) {
                        String name = userInfo.getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = userInfo.getUserName();
                        }
                        FriendRecommendEntry entry = FriendRecommendEntry.getEntry(user, username, appKey);
                        if (null == entry) {
                            if (null != userInfo.getAvatar()) {
                                String path = userInfo.getAvatarFile().getPath();
                                entry = new FriendRecommendEntry(username, userInfo.getNotename(), userInfo.getNickname(), appKey, path,
                                        name, reason, FriendInvitation.INVITED.getValue(), user, 0);
                            } else {
                                entry = new FriendRecommendEntry(username, userInfo.getNotename(), userInfo.getNickname(), appKey, null,
                                        username, reason, FriendInvitation.INVITED.getValue(), user, 0);
                            }
                        } else {
                            entry.state = FriendInvitation.INVITED.getValue();
                            entry.reason = reason;
                        }
                        entry.save();
                        int showNum = SharePreferenceManager.getCachedNewFriendNum() + 1;
                        mContactsView.showNewFriends(showNum);
                        SharePreferenceManager.setCachedNewFriendNum(showNum);
                    }
                }
            });
        } else if (event.getType() == ContactNotifyEvent.Type.contact_deleted) {
            FriendEntry friendEntry = FriendEntry.getFriend(user, username, appKey);
            friendEntry.delete();
            mContactsController.refreshContact();
        }
    }

    public void onEventMainThread(Event event) {
        if (event.getType() == EventType.addFriend) {
            FriendRecommendEntry recommendEntry = FriendRecommendEntry.getEntry(event.getFriendId());
            if (null != recommendEntry) {
                FriendEntry friendEntry = FriendEntry.getFriend(recommendEntry.user,
                        recommendEntry.username, recommendEntry.appKey);
                if (null == friendEntry) {
                    friendEntry = new FriendEntry(recommendEntry.username, recommendEntry.noteName, recommendEntry.nickName, recommendEntry.appKey,
                            recommendEntry.avatar, recommendEntry.displayName,
                            getLetter(recommendEntry.displayName), recommendEntry.user);
                    friendEntry.save();
                    mContactsController.refresh(friendEntry);
                }
            }
        }
    }

    private String getLetter(String name) {
        String letter;
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                .get(name);
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
        return letter;
    }


}
