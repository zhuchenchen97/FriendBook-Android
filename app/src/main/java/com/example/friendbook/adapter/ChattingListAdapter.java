package com.example.friendbook.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import com.example.friendbook.R;
import com.example.friendbook.Application.JGApplication;
import com.example.friendbook.controller.ChatItemController;
import com.example.friendbook.module.ImMsgBean;
import com.example.friendbook.Util.DialogCreator;
import com.example.friendbook.Util.HandleResponseCode;
import com.example.friendbook.Util.TimeFormat;

public class ChattingListAdapter extends BaseAdapter {

    private final int VIEW_TYPE_COUNT = 8;
    public static final int PAGE_MESSAGE_COUNT = 18;

    //文本
    private final int TYPE_SEND_TXT = 0;
    private final int TYPE_RECEIVE_TXT = 1;

    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ImMsgBean> mData;
    private Context mContext;
    private int mWidth;
    private Conversation mConv;
    private List<Message> mMsgList = new ArrayList<Message>();//所有消息列表
    private int mOffset = PAGE_MESSAGE_COUNT;
    private ContentLongClickListener mLongClickListener;
    //当前第0项消息的位置
    private int mStart;
    //发送图片消息的队列
    private Queue<Message> mMsgQueue = new LinkedList<Message>();
    private ChatItemController mController;
    private Dialog mDialog;
    private boolean mHasLastPage = false;


    public ChattingListAdapter(Activity context, Conversation conv) {
        this.mContext = context;
        mActivity = context;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mInflater = LayoutInflater.from(mContext);
        this.mConv = conv;
        this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
        reverse(mMsgList);
        this.mController = new ChatItemController(this, context, conv, mMsgList, dm.density
                );
        mStart = mOffset;
        if (mConv.getType() == ConversationType.single) {
            UserInfo userInfo = (UserInfo) mConv.getTargetInfo();
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
        checkSendingImgMsg();
    }

    public ChattingListAdapter(Context context, Conversation conv, ContentLongClickListener longClickListener,
                               int msgId) {
        this.mContext = context;
        mActivity = (Activity) context;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;

        mInflater = LayoutInflater.from(mContext);
        this.mConv = conv;
        if (mConv.getUnReadMsgCnt() > PAGE_MESSAGE_COUNT) {
            this.mMsgList = mConv.getMessagesFromNewest(0, mConv.getUnReadMsgCnt());
            mStart = mConv.getUnReadMsgCnt();
        } else {
            this.mMsgList = mConv.getMessagesFromNewest(0, mOffset);
            mStart = mOffset;
        }
        reverse(mMsgList);
        mLongClickListener = longClickListener;
        this.mController = new ChatItemController(this, context, conv, mMsgList, dm.density);
        checkSendingImgMsg();

    }

    private void reverse(List<Message> list) {
        if (list.size() > 0) {
            Collections.reverse(list);
        }
    }

    public void dropDownToRefresh() {
        if (mConv != null) {
            List<Message> msgList = mConv.getMessagesFromNewest(mStart, PAGE_MESSAGE_COUNT);
            if (msgList != null) {
                for (Message msg : msgList) {
                    mMsgList.add(0, msg);
                }
                if (msgList.size() > 0) {
                    checkSendingImgMsg();
                    mOffset = msgList.size();
                    mHasLastPage = true;
                } else {
                    mOffset = 0;
                    mHasLastPage = false;
                }
                notifyDataSetChanged();
            }
        }
    }

    public boolean isHasLastPage() {
        return mHasLastPage;
    }

    public int getOffset() {
        return mOffset;
    }

    public void refreshStartPosition() {
        mStart += mOffset;
    }

    //当有新消息加到MsgList，自增mStart
    private void incrementStartPosition() {
        ++mStart;
    }

    /**
     * 检查图片是否处于创建状态，如果是，则加入发送队列
     */
    private void checkSendingImgMsg() {
        for (Message msg : mMsgList) {
            if (msg.getStatus() == MessageStatus.created
                    && msg.getContentType() == ContentType.image) {
                mMsgQueue.offer(msg);
            }
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            if (mConv.getType() == ConversationType.single) {
                UserInfo userInfo = (UserInfo) message.getTargetInfo();
                sendNextImgMsg(message);
            } else {
                sendNextImgMsg(message);
            }

            notifyDataSetChanged();
        }
    }

    public void setSendMsgs(int msgIds) {
        Message msg = mConv.getMessage(msgIds);
        if (msg != null) {
            mMsgList.add(msg);
            incrementStartPosition();
            mMsgQueue.offer(msg);
        }

        if (mMsgQueue.size() > 0) {
            Message message = mMsgQueue.element();
            if (mConv.getType() == ConversationType.single) {
                UserInfo userInfo = (UserInfo) message.getTargetInfo();
                sendNextImgMsg(message);
//                if (userInfo.isFriend()) {
//                    sendNextImgMsg(message);
//                } else {
//                    CustomContent customContent = new CustomContent();
//                    customContent.setBooleanValue("notFriend", true);
//                    Message customMsg = mConv.createSendMessage(customContent);
//                    addMsgToList(customMsg);
//                }
            }

            notifyDataSetChanged();
        }
    }

    /**
     * 从发送队列中出列，并发送图片
     *
     * @param msg 图片消息
     */
    private void sendNextImgMsg(Message msg) {
        JMessageClient.sendMessage(msg);
        msg.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                //出列
                mMsgQueue.poll();
                //如果队列不为空，则继续发送下一张
                if (!mMsgQueue.isEmpty()) {
                    sendNextImgMsg(mMsgQueue.element());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addMsgToList(Message msg) {
        mMsgList.add(msg);
        incrementStartPosition();
        notifyDataSetChanged();
    }

    List<Message> forDel = new ArrayList<>();
    int i;

    //找到撤回的那一条消息,并且用撤回后event下发的去替换掉这条消息在集合中的原位置
    public void delMsgRetract(Message msg) {
        for (Message message : mMsgList) {
            if (msg.getServerMessageId().equals(message.getServerMessageId())) {
                i = mMsgList.indexOf(message);
                forDel.add(message);
            }
        }
        mMsgList.removeAll(forDel);
        mMsgList.add(i, msg);
        notifyDataSetChanged();
    }

    public Message getLastMsg() {
        if (mMsgList.size() > 0) {
            return mMsgList.get(mMsgList.size() - 1);
        } else {
            return null;
        }
    }

    public Message getMessage(int position) {
        return mMsgList.get(position);
    }

    List<Message> del = new ArrayList<>();
    public void removeMessage(Message message) {
        for (Message msg  : mMsgList) {
            if (msg.getServerMessageId().equals(message.getServerMessageId())) {
                del.add(msg);
            }
        }
        mMsgList.removeAll(del);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = mMsgList.get(position);
        //是文字类型或者自定义类型（用来显示群成员变化消息）
        switch (msg.getContentType()) {
            case text:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_TXT
                        : TYPE_RECEIVE_TXT;
            case image:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE
                        : TYPE_RECEIVER_IMAGE;
            default:
                return msg.getDirect() == MessageDirect.send ? TYPE_SEND_IMAGE
                        : TYPE_RECEIVER_IMAGE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 14;
    }


    private View createViewByType(Message msg, int position) {
        // 会话类型
        switch (msg.getContentType()) {
            case text:
                return getItemViewType(position) == TYPE_SEND_TXT ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_text, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_text, null);
            case image:
                return getItemViewType(position) == TYPE_SEND_IMAGE ?
                        mInflater.inflate(R.layout.jmui_chat_item_send_image, null) :
                        mInflater.inflate(R.layout.jmui_chat_item_receive_image, null);
            default:
                return null;
        }
    }

    @Override
    public Message getItem(int position) {
        return mMsgList.get(position);
    }

    public void clearMsgList() {
        mMsgList.clear();
        mStart = 0;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message msg = mMsgList.get(position);
        final UserInfo userInfo = msg.getFromUser();
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByType(msg, position);
            holder.msgTime = (TextView) convertView.findViewById(R.id.jmui_send_time_txt);
            holder.headIcon = (ImageView) convertView.findViewById(R.id.jmui_avatar_iv);
            holder.displayName = (TextView) convertView.findViewById(R.id.jmui_display_name_tv);
            holder.txtContent = (TextView) convertView.findViewById(R.id.jmui_msg_content);
            holder.sendingIv = (ImageView) convertView.findViewById(R.id.jmui_sending_iv);
            //holder.ivDocument = (ImageView) convertView.findViewById(R.id.iv_document);
            switch (msg.getContentType()) {
                case image:
                    holder.picture = (ImageView) convertView.findViewById(R.id.jmui_picture_iv);
                    holder.progressTv = (TextView) convertView.findViewById(R.id.jmui_progress_tv);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        long nowDate = msg.getCreateTime();
        if (mOffset == 18) {
            if (position == 0 || position % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                holder.msgTime.setText(timeFormat.getDetailTime());
                holder.msgTime.setVisibility(View.VISIBLE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过五分钟则显示时间
                if (nowDate - lastDate > 300000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    holder.msgTime.setText(timeFormat.getDetailTime());
                    holder.msgTime.setVisibility(View.VISIBLE);
                } else {
                    holder.msgTime.setVisibility(View.GONE);
                }
            }
        } else {
            if (position == 0 || position == mOffset
                    || (position - mOffset) % 18 == 0) {
                TimeFormat timeFormat = new TimeFormat(mContext, nowDate);

                holder.msgTime.setText(timeFormat.getDetailTime());
                holder.msgTime.setVisibility(View.VISIBLE);
            } else {
                long lastDate = mMsgList.get(position - 1).getCreateTime();
                // 如果两条消息之间的间隔超过五分钟则显示时间
                if (nowDate - lastDate > 300000) {
                    TimeFormat timeFormat = new TimeFormat(mContext, nowDate);
                    holder.msgTime.setText(timeFormat.getDetailTime());
                    holder.msgTime.setVisibility(View.VISIBLE);
                } else {
                    holder.msgTime.setVisibility(View.GONE);
                }
            }
        }


//显示头像
        if (holder.headIcon != null) {
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            holder.headIcon.setImageBitmap(bitmap);
                        } else {
                            holder.headIcon.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
            } else {
                holder.headIcon.setImageResource(R.drawable.jmui_head_icon);
            }

            holder.headIcon.setTag(position);
            holder.headIcon.setOnLongClickListener(mLongClickListener);
        }


        switch (msg.getContentType()) {
            case text:
//                final String content = ((TextContent) msg.getContent()).getText();
//                SimpleCommonUtils.spannableEmoticonFilter(holder.txtContent, content);
                mController.handleTextMsg(msg, holder, position);
                break;
            case image:
                mController.handleImgMsg(msg, holder, position);
                break;

        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView msgTime;
        public ImageView headIcon;
        public ImageView ivDocument;
        public TextView displayName;
        public TextView txtContent;
        public ImageView picture;
        public TextView progressTv;
        public ImageView readStatus;
        public ImageView sendingIv;
        public LinearLayout contentLl;
        public TextView sizeTv;
        public TextView alreadySend;
    }


    public static abstract class ContentLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            onContentLongClick((Integer) v.getTag(), v);
            return true;
        }

        public abstract void onContentLongClick(int position, View view);
    }

}