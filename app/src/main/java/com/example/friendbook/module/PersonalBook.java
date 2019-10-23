package com.example.friendbook.module;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 11972 on 2017/10/28.
 */

public class PersonalBook {
    private String avatarPath;
    private String bookName;
    private String bookAuthor;
    private String bookAddedDate;

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookAddedDate() {
        return bookAddedDate;
    }

    public void setBookAddedDate(String bookAddedDate) {
        this.bookAddedDate = bookAddedDate;
    }
}
