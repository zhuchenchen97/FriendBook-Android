package com.example.friendbook.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.friendbook.R;
import com.example.friendbook.adapter.DynamicRecyclerViewAdapter;
import com.example.friendbook.fragment.ShareFragment;
import com.example.friendbook.module.Dynamic;
import com.example.friendbook.module.DynamicList;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 11972 on 2017/10/15.
 */

public class ShareView {
    private Context mContext;
    private View mRootView;
    private ShareFragment mFragment;
    private int loadCount = 0;
    private XRecyclerView share_list;

    public ShareView(Context mContext, View mRootView, ShareFragment mFragment) {
        this.mContext = mContext;
        this.mRootView = mRootView;
        this.mFragment = mFragment;
    }

    public void initModule(){
        share_list = (XRecyclerView)mRootView.findViewById(R.id.share_recycler_list);
    }

    public void setShareListAdapter(final DynamicRecyclerViewAdapter adapter){
        share_list.setAdapter(adapter);
        share_list.setLayoutManager(new LinearLayoutManager(mContext));
        share_list.setLoadingMoreEnabled(true);
        share_list.setPullRefreshEnabled(true);
        share_list.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        share_list.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        share_list.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadCount = 0;
                adapter.getDynamicList().clear();
                adapter.notifyDataSetChanged();
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
                                                //image.getAVFile("image").getThumbnailUrl(true,)
                                                imagesUrl.add(image.getAVFile("image").getUrl());
                                            }
                                            Dynamic dynamicItem = new Dynamic(mContext,dynamic_id,dynamic_content,dynamic_short_content,dynamic_createAt,creator_userName,imagesUrl,praiseUserNames);
                                            adapter.getDynamicList().add(dynamicItem);
                                            if (adapter.getDynamicList().size()==Dynamic.count){
                                                adapter.notifyDataSetChanged();
                                                share_list.refreshComplete();
                                                share_list.setLoadingMoreEnabled(true);
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
            }

            @Override
            public void onLoadMore() {
                Log.e("ShareView","开始下拉");
                loadCount++;
                AVQuery<AVObject> wholeDynamic = new AVQuery<>("Dynamic");
                final List<Dynamic> selectDynamics = new ArrayList<>();
                wholeDynamic.limit(Dynamic.count);
                wholeDynamic.skip(Dynamic.count*loadCount);
                wholeDynamic.include("user");
                wholeDynamic.orderByDescending("createdAt");
                wholeDynamic.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(final List<AVObject> dynamicList, AVException e) {
                        if (e == null&&dynamicList.size()>0){
                            for (AVObject dynamic:dynamicList){
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
                                                //image.getAVFile("image").getThumbnailUrl(true,)
                                                imagesUrl.add(image.getAVFile("image").getUrl());
                                            }
                                            Dynamic dynamicItem = new Dynamic(mContext,dynamic_id,dynamic_content,dynamic_short_content,dynamic_createAt,creator_userName,imagesUrl,praiseUserNames);
                                            selectDynamics.add(dynamicItem);

                                            if (selectDynamics.size()==dynamicList.size()){
                                                adapter.getDynamicList().addAll(selectDynamics);
                                                adapter.notifyDataSetChanged();
                                                selectDynamics.clear();
                                                share_list.loadMoreComplete();
                                            }

                                        }
                                    }
                                });
                            }

                        }else {
                            share_list.setLoadingMoreEnabled(false);
                        }
                    }
                });


            }
        });
    }


}
