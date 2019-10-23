package com.example.friendbook.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.friendbook.R;
import com.example.friendbook.adapter.PersonalBookshelfListAdapter;
import com.example.friendbook.fragment.BookshelfFragment;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

/**
 * Created by 11972 on 2017/10/15.
 */

public class BookshelfView {
    private View mRootView;
    private BookshelfFragment mFragment;
    private Context mContext;
   // private LinearLayout mSearchHead;//头部的搜索框
    //private LinearLayout mSearch;//mSearchHead布局中的子布局

    private XRecyclerView bookshelf_list;

    public BookshelfView(View mRootView, Context mContext,BookshelfFragment mFragment) {
        this.mRootView = mRootView;
        this.mContext = mContext;
        this.mFragment = mFragment;
    }
    public void initModule(){
        bookshelf_list = (XRecyclerView)mRootView.findViewById(R.id.personal_books_list);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//布局加载器
        bookshelf_list.setLayoutManager(new LinearLayoutManager(mContext));
       // mSearchHead = (LinearLayout)inflater.inflate(R.layout.conversation_head_view,bookshelf_list,false);//
       // mSearch = (LinearLayout)mSearchHead.findViewById(R.id.search_title);
       // bookshelf_list.addHeaderView(mSearchHead);
    }

    public void setBookShelfListAdapter(PersonalBookshelfListAdapter adapter){
        bookshelf_list.setAdapter(adapter);
        bookshelf_list.setLoadingMoreEnabled(true);
        bookshelf_list.setPullRefreshEnabled(true);
        bookshelf_list.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        bookshelf_list.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        bookshelf_list.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {

            }
        });
    }
    /*
    public void setListener(View.OnClickListener onClickListener) {
        mSearch.setOnClickListener(onClickListener);
    }*/

}
