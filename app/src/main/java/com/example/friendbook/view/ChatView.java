package com.example.friendbook.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendbook.R;
import com.example.friendbook.Util.ThreadUtil;
import com.example.friendbook.fragment.ChatFragment;

/**
 * Created by 11972 on 2017/10/15.
 */

public class ChatView {
    private Context mContext;
    private View mRootView;
    private ChatFragment mFragment;

    private ListView mConvListView = null;//对话的list

    private LinearLayout mSearchHead;//头部的搜索框,暂时不用
    private LinearLayout mSearch;//mSearchHead布局中的子布局

    private LinearLayout mHeader;//网络链接断开时出现

    private RelativeLayout mLoadingHeader;//“加载中”
    private ImageView mLoadingIv;//mLoadingHeader布局中的图片
    private LinearLayout mLoadingTv;//mLoadingHeader布局中的文字

    private LinearLayout mNull_conversation;//mRootView中的布局

    private TextView mAllUnReadMsg;//主活动最下边的消息数目text,暂时不用


    public ChatView(Context mContext, View mRootView, ChatFragment mFragment) {
        this.mContext = mContext;
        this.mRootView = mRootView;
        this.mFragment = mFragment;
    }

    public void initModule(){
        mConvListView = (ListView)mRootView.findViewById(R.id.conv_list_view);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//布局加载器
        //mSearchHead = (LinearLayout)inflater.inflate(R.layout.conversation_head_view,mConvListView,false);//mConvListView的三个布局
        mHeader = (LinearLayout)inflater.inflate(R.layout.conv_list_head_view,mConvListView,false);
        mLoadingHeader = (RelativeLayout)inflater.inflate(R.layout.jmui_drop_down_list_header,mConvListView,false);

        //mSearch = (LinearLayout)mSearchHead.findViewById(R.id.search_title);

        mLoadingIv = (ImageView)mLoadingHeader.findViewById(R.id.jmui_loading_img);
        mLoadingTv = (LinearLayout)mLoadingHeader.findViewById(R.id.loading_view);

        mNull_conversation = (LinearLayout)mRootView.findViewById(R.id.null_conversation);

        mConvListView.addHeaderView(mLoadingHeader);
        mConvListView.addHeaderView(mHeader);
        //mConvListView.addHeaderView(mSearchHead);
    }
    public void setConvListAdapter(ListAdapter adapter) {
        mConvListView.setAdapter(adapter);
    }


    public void setListener(View.OnClickListener onClickListener) {
        //mSearch.setOnClickListener(onClickListener);
    }

    public void setItemListeners(AdapterView.OnItemClickListener onClickListener) {
        mConvListView.setOnItemClickListener(onClickListener);
    }

    public void setLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mConvListView.setOnItemLongClickListener(listener);
    }


    public void showHeaderView() {
        mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.VISIBLE);
        mHeader.findViewById(R.id.check_network_hit).setVisibility(View.VISIBLE);
    }

    public void dismissHeaderView() {
        mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.GONE);
        mHeader.findViewById(R.id.check_network_hit).setVisibility(View.GONE);
    }


    public void showLoadingHeader() {
        mLoadingIv.setVisibility(View.VISIBLE);
        mLoadingTv.setVisibility(View.VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIv.getDrawable();
        drawable.start();
    }

    public void dismissLoadingHeader() {
        mLoadingIv.setVisibility(View.GONE);
        mLoadingTv.setVisibility(View.GONE);
    }

    public void setNullConversation(boolean isHaveConv) {
        if (isHaveConv) {
            mNull_conversation.setVisibility(View.GONE);
        } else {
            mNull_conversation.setVisibility(View.VISIBLE);
        }
    }

/*
    public void setUnReadMsg(final int count) {
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAllUnReadMsg != null) {
                    if (count > 0) {
                        mAllUnReadMsg.setVisibility(View.VISIBLE);
                        if (count < 100) {
                            mAllUnReadMsg.setText(count + "");
                        } else {
                            mAllUnReadMsg.setText("99+");
                        }
                    } else {
                        mAllUnReadMsg.setVisibility(View.GONE);
                    }
                }
            }
        });
    }*/

}
