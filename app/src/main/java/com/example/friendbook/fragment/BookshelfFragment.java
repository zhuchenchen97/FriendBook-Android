package com.example.friendbook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.friendbook.R;
import com.example.friendbook.adapter.PersonalBookshelfListAdapter;
import com.example.friendbook.controller.BookshelfController;
import com.example.friendbook.view.BookshelfView;

/**
 * Created by 11972 on 2017/10/3.
 */

public  class BookshelfFragment extends BaseFragment {
    private Activity mContext;
    private View mRootView;
    private BookshelfController mController;
    private BookshelfView bookshelfView;
    protected boolean isCreate = false;

    public static BookshelfFragment newInstance(){
        return new BookshelfFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        isCreate = true;

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_bookshelf,
                (ViewGroup) getActivity().findViewById(R.id.drawer_layout), false);
        bookshelfView = new BookshelfView(mRootView,mContext,this);
        bookshelfView.initModule();
        mController = new BookshelfController(this,bookshelfView);
        //bookshelfView.setListener(mController);
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

}
