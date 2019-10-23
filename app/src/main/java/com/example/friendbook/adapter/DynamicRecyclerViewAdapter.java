package com.example.friendbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.example.friendbook.Application.JGApplication;
import com.example.friendbook.R;
import com.example.friendbook.activity.DynamicActivity;
import com.example.friendbook.activity.FriendInfoActivity;
import com.example.friendbook.module.Dynamic;
import com.example.friendbook.module.DynamicList;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.NineGridViewAdapter;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 11972 on 2017/10/6.
 */

public class DynamicRecyclerViewAdapter extends RecyclerView.Adapter<DynamicRecyclerViewAdapter.DynamicViewHolder> {
    private List<Dynamic> dynamicList;
    private Context mContext;
    private int count = 0;
    public DynamicRecyclerViewAdapter(List<Dynamic> dynamicList,Context context) {
        super();
        this.dynamicList = dynamicList;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(final DynamicViewHolder holder, int position) {
        final StringBuilder builder = new StringBuilder();
        final Dynamic dynamic = dynamicList.get(position);
        JMessageClient.getUserInfo(dynamic.getUserInfo(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0){
                    Picasso.with(mContext).load(userInfo.getAvatarFile()).placeholder(R.drawable.user_image).into(holder.share_user_image);
                    //holder.share_user_name.setText(userInfo.getNickname());
                    if (TextUtils.isEmpty(userInfo.getNickname())){
                        holder.share_user_name.setText(userInfo.getUserName());
                    }else {
                        holder.share_user_name.setText(userInfo.getNickname());
                    }
                }
            }
        });

        List<ImageInfo>  imageInfos = new ArrayList<>();
        for (String url:dynamic.getDynamic_images()){
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(url);
            imageInfo.setBigImageUrl(url);
            imageInfos.add(imageInfo);
        }
        holder.share_images.setAdapter(new NineGridViewClickAdapter(mContext,imageInfos));
        holder.share_date.setText(dynamic.getDynamic_time());
        holder.share_name.setText(dynamic.getDynamic_content());

        count = dynamic.getPraiseUserInfos().size();
        if (count == 0){
            holder.share_favorite_text.setText("");
        }else{
            final int count1 = count;
            final List<String> userNicks = new ArrayList<>();
            for (final String userName:dynamic.getPraiseUserInfos()){
                if (userName.equals( JMessageClient.getMyInfo().getUserName())){
                    holder.share_favorite_checkbox.setChecked(true);
                }
                JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i==0){
                            if (userName.equals( JMessageClient.getMyInfo().getUserName())){
                                if (!TextUtils.isEmpty(userInfo.getNickname())){
                                    userNicks.add(0,userInfo.getNickname());
                                    Log.e("Adapter",userNicks.get(0));
                                }else {
                                    userNicks.add(0,userName);
                                }
                            }else {
                                if (!TextUtils.isEmpty(userInfo.getNickname())){
                                    userNicks.add(userInfo.getNickname());
                                }else {
                                    userNicks.add(userName);
                                }
                            }

                            if (userNicks.size() == count1){
                                for (int j = 0; j< count1; j++){
                                    builder.append(userNicks.get(j));
                                    if (j< count1 -1){
                                        builder.append("、");
                                    }
                                }
                                holder.share_favorite_text.setText(builder.toString()+"等"+ count1 +"个人觉得很赞！");
                            }
                        }

                    }
                });
            }
        }

        holder.share_favorite_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String myName = "";
                if (!TextUtils.isEmpty(JMessageClient.getMyInfo().getNickname())){
                    myName = JMessageClient.getMyInfo().getNickname();
                }else {
                    myName = JMessageClient.getMyInfo().getUserName();
                }
                AVObject Dynamic = AVObject.createWithoutData("Dynamic",dynamic.getDynamic_id());
                if (isChecked){
                    dynamic.getPraiseUserInfos().add(JMessageClient.getMyInfo().getUserName());
                    Dynamic.put("praise",dynamic.getPraiseUserInfos());
                    final String finalMyName = myName;
                    Dynamic.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                if (dynamic.getPraiseUserInfos().size() == 1){
                                    builder.append(finalMyName);
                                }else {
                                    builder.insert(0, finalMyName +"、");
                                }
                                holder.share_favorite_text.setVisibility(View.VISIBLE);
                                count++;
                                holder.share_favorite_text.setText(builder.toString()+"等"+ count +"个人觉得很赞！");
                            }

                        }
                    });

                }else {
                    dynamic.getPraiseUserInfos().remove(JMessageClient.getMyInfo().getUserName());
                    Dynamic.put("praise",dynamic.getPraiseUserInfos());
                    final String finalMyName1 = myName;
                    Dynamic.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                if (dynamic.getPraiseUserInfos().isEmpty()){
                                    count = 0;
                                    builder.setLength(0);
                                    holder.share_favorite_text.setText("");
                                    holder.share_favorite_text.setVisibility(View.GONE);
                                }else {
                                    builder.delete(0, finalMyName1.length()+1);
                                    count--;
                                    holder.share_favorite_text.setText(builder.toString()+"等"+ count +"个人觉得很赞！");
                                }

                            }

                        }
                    });
                }
            }
        });

        //holder.share_favorite_text.setText();
    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_list_item,parent,false);
        final DynamicViewHolder holder = new DynamicViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition()-1;
                Dynamic dynamic = dynamicList.get(position);
                Intent intent = new Intent(mContext,DynamicActivity.class);
                intent.putExtra("dynamic_id",dynamic.getDynamic_id());
                intent.putExtra("dynamic",dynamic);
                intent.putExtra("ischeck",holder.share_favorite_checkbox.isChecked());
                intent.putExtra("praise",holder.share_favorite_text.getText().toString());
                //intent.putExtra("user_image",holder.share_user_image.)
                mContext.startActivity(intent);
            }
        });
        holder.share_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dynamic dynamic = dynamicList.get(position);
                JMessageClient.getUserInfo(dynamic.getUserInfo(), new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i == 0){
                            Intent intent = new Intent(mContext, FriendInfoActivity.class);
                            intent.putExtra("fromContact", true);
                            intent.putExtra(JGApplication.TARGET_ID,userInfo.getUserName());
                            intent.putExtra(JGApplication.TARGET_APP_KEY,userInfo.getAppKey() );
                            mContext.startActivity(intent);
                        }
                        else
                            Toast.makeText(mContext,"获取用户信息出错",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return holder;
    }

    public class DynamicViewHolder extends RecyclerView.ViewHolder{
        private ImageView share_user_image;
        private TextView share_user_name;
        private TextView share_date;
        private TextView share_name;
        private NineGridView share_images;
        private CheckBox share_favorite_checkbox;
        private ImageButton share_comment_button;
        private TextView share_favorite_text;
        private View share_line;
        private View itemView;


        private DynamicViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            share_images = (NineGridView)itemView.findViewById(R.id.nine_gridview);
            share_user_image = (ImageView)itemView.findViewById(R.id.share_user_image);
            share_user_name = (TextView)itemView.findViewById(R.id.share_user_name);
            share_date = (TextView)itemView.findViewById(R.id.share_time);
            share_name = (TextView)itemView.findViewById(R.id.share_content);
            share_favorite_checkbox = (CheckBox) itemView.findViewById(R.id.share_favorite_checkbox);
            share_comment_button = (ImageButton) itemView.findViewById(R.id.share_comment_button);
            share_favorite_text = (TextView) itemView.findViewById(R.id.share_favorite_text);
            share_line = itemView.findViewById(R.id.share_line);
        }

    }

    public List<Dynamic> getDynamicList() {
        return dynamicList;
    }
}
