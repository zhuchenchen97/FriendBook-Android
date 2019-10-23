package com.example.friendbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.im.android.api.model.Conversation;
import com.example.friendbook.R;
import com.example.friendbook.activity.ChatActivity;
import com.example.friendbook.adapter.ChattingListAdapter;
import com.example.friendbook.view.listview.DropDownListView;

/**
 * Created by ${chenyn} on 2017/3/28.
 */

public class ChatActivityView extends RelativeLayout {
    Context mContext;
    private ImageButton mReturnButton;
    //private ImageButton mRightBtn;
    private DropDownListView mChatListView;
    private Conversation mConv;

    private TextView mChatTitle;

    public ChatActivityView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ChatActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatActivityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initModule(float density, int densityDpi) {
        mReturnButton = (ImageButton) findViewById(R.id.jmui_return_btn);
        //mRightBtn = (ImageButton) findViewById(R.id.jmui_right_btn);
        mChatTitle = (TextView) findViewById(R.id.jmui_title);
        if (densityDpi <= 160) {
            mChatTitle.setMaxWidth((int)(180 * density + 0.5f));
        }else if (densityDpi <= 240) {
            mChatTitle.setMaxWidth((int)(190 * density + 0.5f));
        }else {
            mChatTitle.setMaxWidth((int)(200 * density + 0.5f));
        }
        mChatListView = (DropDownListView) findViewById(R.id.lv_chat);

    }

    public void setToPosition(int position) {
        mChatListView.smoothScrollToPosition(position);
    }

    public void setChatListAdapter(ChattingListAdapter chatAdapter) {
        mChatListView.setAdapter(chatAdapter);
    }

    public DropDownListView getListView() {
        return mChatListView;
    }
    public void setToBottom() {
        mChatListView.clearFocus();
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
            }
        });
    }
    public void setConversation(Conversation conv) {
        this.mConv = conv;
    }


    public void setListeners(ChatActivity listeners) {
        mReturnButton.setOnClickListener(listeners);
       // mRightBtn.setOnClickListener(listeners);
    }
/*
    public void dismissRightBtn() {
        mRightBtn.setVisibility(View.GONE);
    }

    public void showRightBtn() {
        mRightBtn.setVisibility(View.VISIBLE);
    }*/

    public void setChatTitle(int id, int count) {
        mChatTitle.setText(id);
    }

    public void setChatTitle(int id) {
        mChatTitle.setText(id);
    }


    //设置群聊名字
    public void setChatTitle(String name, int count) {
        mChatTitle.setText(name);
    }

    public void setChatTitle(String title) {
        mChatTitle.setText(title);
    }
}
