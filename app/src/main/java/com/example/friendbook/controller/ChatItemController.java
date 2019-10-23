package com.example.friendbook.controller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.example.friendbook.R;
//import com.example.friendbook.activity.BrowserViewPagerActivity;
import com.example.friendbook.adapter.ChattingListAdapter;
import com.example.friendbook.adapter.ChattingListAdapter.ViewHolder;
import com.example.friendbook.Application.JGApplication;
//import com.example.friendbook.pickerimage.utils.BitmapDecoder;
import com.example.friendbook.Util.HandleResponseCode;
import com.example.friendbook.Util.SimpleCommonUtils;
import com.example.friendbook.Util.ToastUtil;


public class ChatItemController {

    private ChattingListAdapter mAdapter;
    private Context mContext;
    private Conversation mConv;
    private List<Message> mMsgList;
    private ChattingListAdapter.ContentLongClickListener mLongClickListener;
    private float mDensity;
    //public Animation mSendingAnim;


    private boolean autoPlay = false;
    private int nextPlayPosition = 0;
    private boolean mIsEarPhoneOn;
    private int mSendMsgId;
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private UserInfo mUserInfo;

    public ChatItemController(ChattingListAdapter adapter, Context context, Conversation conv, List<Message> msgList,
                              float density) {
        this.mAdapter = adapter;
        this.mContext = context;
        this.mConv = conv;
        if (mConv.getType() == ConversationType.single) {
            mUserInfo = (UserInfo) mConv.getTargetInfo();
        }
        this.mMsgList = msgList;
        this.mDensity = density;
        //mSendingAnim = AnimationUtils.loadAnimation(mContext, R.anim.jmui_rotate);
        //LinearInterpolator lin = new LinearInterpolator();
        //mSendingAnim.setInterpolator(lin);
    }

    public void handleTextMsg(final Message msg, final ViewHolder holder, int position) {
        final String content = ((TextContent) msg.getContent()).getText();
        SimpleCommonUtils.spannableEmoticonFilter(holder.txtContent, content);
        holder.txtContent.setText(content);
        holder.txtContent.setTag(position);
        holder.txtContent.setOnLongClickListener(mLongClickListener);
        // 检查发送状态，发送方有重发机制
        if (msg.getDirect() == MessageDirect.send) {
            switch (msg.getStatus()) {
                case created:
                    if (null != mUserInfo) {
                        holder.sendingIv.setVisibility(View.GONE);

                    }
                    break;
                case send_success:
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);

                    break;
                case send_going:
                    sendingTextOrVoice(holder, msg);
                    break;
                default:
            }

        } else {

        }
    }

    // 处理图片
    public void handleImgMsg(final Message msg, final ViewHolder holder, final int position) {
        final ImageContent imgContent = (ImageContent) msg.getContent();
        final String jiguang = imgContent.getStringExtra("jiguang");
        // 先拿本地缩略图
        final String path = imgContent.getLocalThumbnailPath();
        if (path == null) {
            //从服务器上拿缩略图
            imgContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                @Override
                public void onComplete(int status, String desc, File file) {
                    if (status == 0) {
                        ImageView imageView = setPictureScale(jiguang, msg, file.getPath(), holder.picture);
                        Picasso.with(mContext).load(file).into(imageView);
                    }
                }
            });
        } else {
            ImageView imageView = setPictureScale(jiguang, msg, path, holder.picture);
            Picasso.with(mContext).load(new File(path)).into(imageView);
        }

        // 接收图片
        if (msg.getDirect() == MessageDirect.receive) {

            switch (msg.getStatus()) {
                case receive_fail:
                    holder.picture.setImageResource(R.drawable.jmui_fetch_failed);
                    break;
                default:
            }
            // 发送图片方，直接加载缩略图
        } else {
//            try {
//                setPictureScale(path, holder.picture);
//                Picasso.with(mContext).load(new File(path)).into(holder.picture);
//            } catch (NullPointerException e) {
//                Picasso.with(mContext).load(IdHelper.getDrawable(mContext, "jmui_picture_not_found"))
//                        .into(holder.picture);
//            }
            //检查状态
            switch (msg.getStatus()) {
                case created:
                    holder.picture.setEnabled(false);

                    holder.sendingIv.setVisibility(View.VISIBLE);

                    holder.progressTv.setText("0%");
                    break;
                case send_success:
                    holder.picture.setEnabled(true);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.picture.setAlpha(1.0f);
                    holder.progressTv.setVisibility(View.GONE);

                    break;

                case send_going:
                    holder.picture.setEnabled(false);

                    sendingImage(msg, holder);
                    break;
                default:
                    holder.picture.setAlpha(0.75f);
                    holder.sendingIv.setVisibility(View.VISIBLE);
                    //holder.sendingIv.startAnimation(mSendingAnim);
                    holder.progressTv.setVisibility(View.VISIBLE);
                    holder.progressTv.setText("0%");
                    //从别的界面返回聊天界面，继续发送
                    if (!mMsgQueue.isEmpty()) {
                        Message message = mMsgQueue.element();
                        if (message.getId() == msg.getId()) {
                            JMessageClient.sendMessage(message);
                            mSendMsgId = message.getId();
                            sendingImage(message, holder);
                        }
                    }
            }
        }
        if (holder.picture != null) {
            // 点击预览图片
            holder.picture.setOnClickListener(new BtnOrTxtListener(position, holder));
            holder.picture.setTag(position);
            holder.picture.setOnLongClickListener(mLongClickListener);

        }
    }

    private void sendingImage(final Message msg, final ViewHolder holder) {
        holder.picture.setAlpha(0.75f);
        holder.sendingIv.setVisibility(View.VISIBLE);
        //holder.sendingIv.startAnimation(mSendingAnim);
        holder.progressTv.setVisibility(View.VISIBLE);
        holder.progressTv.setText("0%");

        //如果图片正在发送，重新注册上传进度Callback
        if (!msg.isContentUploadProgressCallbackExists()) {
            msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                @Override
                public void onProgressUpdate(double v) {
                    String progressStr = (int) (v * 100) + "%";
                    holder.progressTv.setText(progressStr);
                }
            });
        }
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, String desc) {
                    if (!mMsgQueue.isEmpty() && mMsgQueue.element().getId() == mSendMsgId) {
                        mMsgQueue.poll();
                        if (!mMsgQueue.isEmpty()) {
                            Message nextMsg = mMsgQueue.element();
                            JMessageClient.sendMessage(nextMsg);
                            mSendMsgId = nextMsg.getId();
                        }
                    }
                    holder.picture.setAlpha(1.0f);
                    holder.sendingIv.clearAnimation();
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.progressTv.setVisibility(View.GONE);
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        mAdapter.addMsgToList(customMsg);
                    }

                    Message message = mConv.getMessage(msg.getId());
                    mMsgList.set(mMsgList.indexOf(msg), message);
//                    notifyDataSetChanged();
                }
            });

        }
    }

    //正在发送文字或语音
    private void sendingTextOrVoice(final ViewHolder holder, final Message msg) {
        holder.sendingIv.setVisibility(View.VISIBLE);
        //holder.sendingIv.startAnimation(mSendingAnim);

        //消息正在发送，重新注册一个监听消息发送完成的Callback
        if (!msg.isSendCompleteCallbackExists()) {
            msg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    holder.sendingIv.setVisibility(View.GONE);
                    holder.sendingIv.clearAnimation();
                    if (status == 803008) {
                        CustomContent customContent = new CustomContent();
                        customContent.setBooleanValue("blackList", true);
                        Message customMsg = mConv.createSendMessage(customContent);
                        mAdapter.addMsgToList(customMsg);
                    }  else if (status != 0) {
                        HandleResponseCode.onHandle(mContext, status, false);
                    }
                }
            });
        } else {
            holder.sendingIv.setVisibility(View.GONE);
            holder.sendingIv.clearAnimation();
        }
    }






    public class BtnOrTxtListener implements View.OnClickListener {

        private int position;
        private ViewHolder holder;

        public BtnOrTxtListener(int index, ViewHolder viewHolder) {
            this.position = index;
            this.holder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            Message msg = mMsgList.get(position);
            MessageDirect msgDirect = msg.getDirect();
            switch (msg.getContentType()) {
                case image:
                    /*if (holder.picture != null && v.getId() == holder.picture.getId()) {
                        Intent intent = new Intent();
                        intent.putExtra(JGApplication.TARGET_ID, mConv.getTargetId());
                        intent.putExtra("msgId", msg.getId());
                        intent.putExtra(JGApplication.TARGET_APP_KEY, mConv.getTargetAppKey());
                        intent.putExtra("msgCount", mMsgList.size());
                        intent.putIntegerArrayListExtra(JGApplication.MsgIDs, getImgMsgIDList());
                        intent.putExtra("fromChatActivity", true);
                        intent.setClass(mContext, BrowserViewPagerActivity.class);
                        mContext.startActivity(intent);
                    }*/
                    break;
            }

        }
    }


    /**
     * 设置图片最小宽高
     *
     * @param path      图片路径
     * @param imageView 显示图片的View
     */
    private ImageView setPictureScale(String extra, Message message, String path, final ImageView imageView) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);


        //计算图片缩放比例
        double imageWidth = opts.outWidth;
        double imageHeight = opts.outHeight;
        return setDensity(extra, message, imageWidth, imageHeight, imageView);
    }

    private ImageView setDensity(String extra, Message message, double imageWidth, double imageHeight, ImageView imageView) {
        if (extra != null) {
            imageWidth = 200;
            imageHeight = 200;
        } else {
            if (imageWidth > 300) {
                imageWidth = 550;
                imageHeight = 250;
            } else if (imageHeight > 450) {
                imageWidth = 300;
                imageHeight = 450;
            } else if ((imageWidth < 50 && imageWidth > 20) || (imageHeight < 50 && imageHeight > 20)) {
                imageWidth = 200;
                imageHeight = 300;
            } else if (imageWidth < 20 || imageHeight < 20) {
                imageWidth = 100;
                imageHeight = 150;
            } else {
                imageWidth = 300;
                imageHeight = 450;
            }
        }

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) imageWidth;
        params.height = (int) imageHeight;
        imageView.setLayoutParams(params);

        return imageView;
    }


    private DisplayImageOptions options = createImageOptions();

    private boolean hasLoaded = false;

    private static final DisplayImageOptions createImageOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }







    private ArrayList<Integer> getImgMsgIDList() {
        ArrayList<Integer> imgMsgIDList = new ArrayList<Integer>();
        for (Message msg : mMsgList) {
            if (msg.getContentType() == ContentType.image) {
                imgMsgIDList.add(msg.getId());
            }
        }
        return imgMsgIDList;
    }

    private void browseDocument(String fileName, String path) {
        try {
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext);
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法打开该类型的文件", Toast.LENGTH_SHORT).show();
        }
    }

}
