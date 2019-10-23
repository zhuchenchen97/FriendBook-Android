package com.example.friendbook.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.friendbook.activity.LoginActivity;
import com.example.friendbook.adapter.DynamicRecyclerViewAdapter;
import com.example.friendbook.fragment.ShareFragment;
import com.example.friendbook.module.Dynamic;
import com.example.friendbook.module.DynamicList;
import com.example.friendbook.view.ShareView;
import com.lzy.ninegrid.ImageInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 11972 on 2017/10/15.
 */

public class ShareController {
    private DynamicRecyclerViewAdapter adapter;
    private ShareView mShareView;
    private ShareFragment mContext;
    private int mWidth;
    private List<Dynamic> dynamicList = new ArrayList<>();
    public ShareController(ShareView mShareView, ShareFragment mContext, int mWidth) {
        this.mShareView = mShareView;
        this.mContext = mContext;
        this.mWidth = mWidth;
        initDynamicAdapter();
    }
    private void initDynamicAdapter(){
        AVQuery<AVObject> wholeDynamic = new AVQuery<>("Dynamic");
        wholeDynamic.limit(Dynamic.count);
        //wholeDynamic.skip(2*i);
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
                        AVQuery<AVObject> dynamic_images = new AVQuery<>("DynamicImages");
                        dynamic_images.whereEqualTo("targetDynamic",dynamic);
                        dynamic_images.include("image");
                        dynamic_images.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null){
                                    for (AVObject image:list){
                                        imagesUrl.add(image.getAVFile("image").getUrl());
                                    }
                                    Dynamic dynamicItem = new Dynamic(mContext.getContext(),dynamic_id,dynamic_content,dynamic_short_content,dynamic_createAt,creator_userName,imagesUrl,praiseUserNames);
                                    dynamicList.add(dynamicItem);
                                    if (dynamicList.size()==Dynamic.count){
                                        adapter = new DynamicRecyclerViewAdapter(dynamicList,mContext.getActivity());
                                        mShareView.setShareListAdapter(adapter);
                                    }
                                }
                            }
                        });
                    }

                }
            }
        });
    }



}
