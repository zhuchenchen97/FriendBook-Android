package com.example.friendbook.module;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 11972 on 2017/10/6.
 */

public class DynamicList {
    public static List<Dynamic> getWholeDynamic(final Context context,int i){
        final List<Dynamic> lists = new ArrayList<>();
        AVQuery<AVObject> wholeDynamic = new AVQuery<>("Dynamic");
        wholeDynamic.limit(2);
        wholeDynamic.skip(2*i);
        wholeDynamic.include("user");
        wholeDynamic.orderByDescending("createdAt");
        wholeDynamic.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    Log.e("DynamicList","获得数据");
                    for (AVObject dynamic:list){
                        final List<String> praiseUserNames = dynamic.getList("praise");
                        final List<String> imagesUrl = new ArrayList<>();
                        final String dynamic_id = dynamic.getObjectId();
                        final String dynamic_short_content = dynamic.getString("dynamic_short_content");
                        final String dynamic_content = dynamic.getString("dynamic_content");
                        final Date dynamic_createAt = dynamic.getCreatedAt();
                        final String creator_userName = dynamic.getAVUser("user").getUsername();
                        /*AVQuery<AVObject> dynamic_images = new AVQuery<>("DynamicImages");
                        dynamic_images.whereEqualTo("targetDynamic",dynamic);
                        dynamic_images.include("image");
                        dynamic_images.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null){
                                    for (AVObject image:list){
                                        //image.getAVFile("image").getThumbnailUrl(true,)
                                        imagesUrl.add(image.getAVFile("image").getUrl());
                                    }
                                }
                            }
                        });*/
                        Dynamic dynamicItem = new Dynamic(context,dynamic_id,dynamic_content,dynamic_short_content,dynamic_createAt,creator_userName,imagesUrl,praiseUserNames);
                        lists.add(dynamicItem);
                    }

                    }
                }
        });
        return lists;
    }

}
