package com.example.friendbook.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendbook.R;
import com.example.friendbook.controller.ChatController;
import com.example.friendbook.module.PersonalBook;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 11972 on 2017/10/28.
 */

public class PersonalBookshelfListAdapter extends RecyclerView.Adapter<PersonalBookshelfListAdapter.BookshelfViewHolder>{
    private List<PersonalBook> personalBookList;
    private Context mContext;
    public PersonalBookshelfListAdapter(List<PersonalBook> personalBookList, Context mContext) {
        super();
        this.mContext = mContext;
        this.personalBookList = personalBookList;
    }

    @Override
    public BookshelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_book_list,parent,false);
        BookshelfViewHolder viewHolder = new BookshelfViewHolder(v);
        int position = viewHolder.getAdapterPosition();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"进入图书具体详情的活动",Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BookshelfViewHolder holder, int position) {
        PersonalBook book = personalBookList.get(position);
        Picasso.with(mContext).load(book.getAvatarPath()).error(R.drawable.picture).into(holder.bookImage);
        holder.bookName.setText(book.getBookName());
        holder.bookAuthor.setText(book.getBookAuthor());
        holder.bookAddedDate.setText(book.getBookAddedDate());
    }

    @Override
    public int getItemCount() {
        return personalBookList.size();
    }

    public class BookshelfViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookImage;
        private TextView bookName;
        private TextView bookAuthor;
        private TextView bookAddedDate;
        private View itemView;
        public BookshelfViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            bookImage = (ImageView) itemView.findViewById(R.id.personal_books_list_image);
            bookName = (TextView) itemView.findViewById(R.id.personal_books_list_name);
            bookAuthor = (TextView) itemView.findViewById(R.id.personal_books_list_anthor);
            bookAddedDate = (TextView) itemView.findViewById(R.id.personal_books_list_adddate);
        }


    }

}
