package com.example.friendbook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.friendbook.R;
import com.example.friendbook.adapter.DynamicRecyclerViewAdapter;
import com.example.friendbook.controller.ShareController;
import com.example.friendbook.module.Dynamic;
import com.example.friendbook.module.DynamicList;
import com.example.friendbook.view.ShareView;
import com.lzy.ninegrid.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11972 on 2017/10/3.
 */

public class ShareFragment extends BaseFragment {
    private Activity mContext;
    private View mRootView;
    protected boolean isCreate = false;
    private ShareView mShareView;
    private ShareController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        isCreate = true;

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_share,
                (ViewGroup) getActivity().findViewById(R.id.drawer_layout), false);
        mShareView = new ShareView(mContext,mRootView,this);
        mShareView.initModule();
        mController = new ShareController(mShareView,this,mWidth);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public static ShareFragment newInstance(){
        return new ShareFragment();
    }


}
