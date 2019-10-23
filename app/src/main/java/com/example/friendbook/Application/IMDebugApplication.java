package com.example.friendbook.Application;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.avos.avoscloud.AVOSCloud;
import com.example.friendbook.Util.SharePreferenceManager;
import com.example.friendbook.database.UserEntry;
import com.example.friendbook.entity.NotificationClickEventReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by 11972 on 2017/10/15.
 */

public class IMDebugApplication extends Application {
    private static final String JCHAT_CONFIGS = "JChat_configs";
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"OPmrF3c342gnJA9of6W7BzC8-gzGzoHsz","ynHka0JmV1sa7rh2t9Pq11Sw");
        AVOSCloud.setDebugLogEnabled(true);
        ActiveAndroid.initialize(this);
        Fresco.initialize(getApplicationContext());

        JMessageClient.setDebugMode(true);
        JMessageClient.init(this,true);
        SharePreferenceManager.init(getApplicationContext(),JCHAT_CONFIGS );
        //设置Notification的模式
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
