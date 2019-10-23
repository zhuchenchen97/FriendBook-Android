package com.example.friendbook.controller;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.example.friendbook.R;
import com.example.friendbook.adapter.PersonalBookshelfListAdapter;
import com.example.friendbook.module.PersonalBook;
import com.example.friendbook.view.BookshelfView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11972 on 2017/10/15.
 */

public class BookshelfController {
    private PersonalBookshelfListAdapter adapter;
    private List<PersonalBook> personalBookList = new ArrayList<>();
    private Fragment mContext;
    private BookshelfView mBookshelfView;

    public BookshelfController(Fragment mContext, BookshelfView mBookshelfView) {
        this.mContext = mContext;
        this.mBookshelfView = mBookshelfView;
        initPersonalBookList();
        initAdapter();
    }
    private void initAdapter(){
        adapter = new PersonalBookshelfListAdapter(personalBookList,mContext.getActivity());
        mBookshelfView.setBookShelfListAdapter(adapter);
    }
    private void initPersonalBookList(){
        PersonalBook book = new PersonalBook();
        book.setAvatarPath("https://img1.doubanio.com/lpic/s1305338.jpg");
        book.setBookName("他改变了中国");
        book.setBookAuthor("[美] 罗伯特.劳伦斯.库恩");
        book.setBookAddedDate("2017.10.28");
        personalBookList.add(book);
        book = new PersonalBook();
        book.setAvatarPath("https://img3.doubanio.com/lpic/s28397415.jpg");
        book.setBookName("二手时间");
        book.setBookAuthor("[白俄] S·A·阿列克谢耶维奇 ");
        book.setBookAddedDate("2017.10.29");
        personalBookList.add(book);
         book = new PersonalBook();
        book.setAvatarPath("https://img3.doubanio.com/lpic/s27073965.jpg");
        book.setBookName("邓小平时代");
        book.setBookAuthor("[美] 傅高义");
        book.setBookAddedDate("2017.09.26");
        personalBookList.add(book);
        book = new PersonalBook();
        book.setAvatarPath("https://img3.doubanio.com/view/ark_article_cover/retina/public/1610056.jpg?v=1395394316.0");
        book.setBookName("看见");
        book.setBookAuthor("柴静");
        book.setBookAddedDate("2017.11.26");
        personalBookList.add(book);
    }


}
