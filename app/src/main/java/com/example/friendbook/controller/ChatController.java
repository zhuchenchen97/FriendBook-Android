package com.example.friendbook.controller;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.example.friendbook.R;
import com.example.friendbook.Util.DialogCreator;
import com.example.friendbook.Util.SortConvList;
import com.example.friendbook.activity.ChatActivity;
import com.example.friendbook.activity.SearchContactsActivity;
import com.example.friendbook.adapter.ConversationListAdapter;
import com.example.friendbook.fragment.ChatFragment;
import com.example.friendbook.view.ChatView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 11972 on 2017/10/15.
 */

public class ChatController implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    public static final String CONV_TITLE = "conv_title";
    public static final String DRAFT = "draft";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    private List<Conversation> mConv = new ArrayList<Conversation>();
    private Dialog mDialog;
    private int mWidth;
    private ConversationListAdapter mListAdapter;

    private ChatView mConvListView;
    private ChatFragment mContext;

    public ChatController(int mWidth, ChatView mConvListView, ChatFragment mContext) {
        this.mWidth = mWidth;
        this.mConvListView = mConvListView;
        this.mContext = mContext;
        initConvListAdapter();
    }
    //得到会话列表
    private void initConvListAdapter() {
        mDatas = JMessageClient.getConversationList();
        if (mDatas != null && mDatas.size() > 0) {
            mConvListView.setNullConversation(true);
            SortConvList sortConvList = new SortConvList();
            Collections.sort(mDatas, sortConvList);
         /*   for (int i = 0; i < SharePreferenceManager.getTopSize(); i++) {
                ConversationEntry topConversation = ConversationEntry.getTopConversation(i);
                for (int x = 0; x < mDatas.size(); x++) {
                    if (topConversation.targetname.equals(mDatas.get(x).getTargetId())) {
                        mConv.add(mDatas.get(x));
                        mDatas.remove(mDatas.get(x));

                        mDatas.add(i, mConv.get(0));
                        mConv.clear();
                        break;

                    }
                }
            }*/
        } else {
            mConvListView.setNullConversation(false);
        }
        mListAdapter = new ConversationListAdapter(mContext.getActivity(), mDatas, mConvListView);
        mConvListView.setConvListAdapter(mListAdapter);
    }

    @Override
    public void onClick(View v) {
        //搜索框的点击事件
        switch (v.getId()) {
            case R.id.search_title:
                Intent intent = new Intent();
                intent.setClass(mContext.getActivity(), SearchContactsActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击会话条目
        Intent intent = new Intent();
        if (position > 0) {
            //这里-3是减掉添加的三个headView
            Conversation conv = mDatas.get(position - 2);
            intent.putExtra(CONV_TITLE, conv.getTitle());
            //单聊
                String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
                intent.putExtra(TARGET_ID, targetId);
                intent.putExtra(TARGET_APP_KEY, conv.getTargetAppKey());
                intent.putExtra(DRAFT, getAdapter().getDraft(conv.getId()));
            intent.setClass(mContext.getActivity(), ChatActivity.class);
            mContext.getContext().startActivity(intent);

        }
    }
    public ConversationListAdapter getAdapter() {
        return mListAdapter;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Conversation conv = mDatas.get(position - 3);
        if (conv != null) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        //会话置顶
                        /*case R.id.jmui_top_conv_ll:
                            mListAdapter.setConvTop(conv);
                            int topSize = SharePreferenceManager.getTopSize();
                            ConversationEntry entry = new ConversationEntry(conv.getTargetId(), topSize);
                            entry.save();
                            ++topSize;
                            SharePreferenceManager.setTopSize(topSize);
                            mDialog.dismiss();
                            break;*/
                        //删除会话
                        case R.id.jmui_delete_conv_ll:
                            JMessageClient.deleteSingleConversation(((UserInfo) conv.getTargetInfo()).getUserName());
                            mDatas.remove(position - 3);
                            if (mDatas.size() > 0) {
                                mConvListView.setNullConversation(true);
                            } else {
                                mConvListView.setNullConversation(false);
                            }
                            mListAdapter.notifyDataSetChanged();
                            mDialog.dismiss();
                            break;
                        default:
                            break;
                    }

                }
            };
            mDialog = DialogCreator.createDelConversationDialog(mContext.getActivity(), listener);
            mDialog.show();
            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        return true;
    }
}
