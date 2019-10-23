package com.example.friendbook.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.friendbook.R;
import com.example.friendbook.module.D_Floor;
import com.example.friendbook.module.Dynamic;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 12410 on 2017/11/3.
 */

public class D_FloorAdapter extends RecyclerView.Adapter<D_FloorAdapter.ViewHolder> {
    private List<D_Floor> mFloorList;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private View VIEW_FOOTER;
    private View VIEW_HEADER;
    //Type
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView floorHeadimage;
        TextView floorName;
        TextView floorTime;
        TextView floorContent;
        TextView floorNumber;
        public ViewHolder(View view){
            super(view);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//待更新WRAP_CONTENT
            view.setLayoutParams(params);//待改
            floorHeadimage = (ImageView)view.findViewById(R.id.floorheadImage);
            floorName = (TextView) view.findViewById(R.id.floornameView);
            floorTime = (TextView)view.findViewById(R.id.floortimeView);
            floorContent = (TextView)view.findViewById(R.id.floorcontentView);
            floorNumber = (TextView)view.findViewById(R.id.floornumberView);
        }
    }

    public D_FloorAdapter(List<D_Floor> floorList,Context mContext){
        mFloorList = floorList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new ViewHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new ViewHolder(VIEW_HEADER);
        } else {
            return new ViewHolder(getLayout(R.layout.card_floor));
        }
    }
    /*@Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_floor,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        D_Floor d_floor = mFloorList.get(position);
        holder.floorHeadimage.setImageResource(d_floor.get_Floor_HeadId());
        holder.floorName.setText(d_floor.get_Floor_Name());
        holder.floorTime.setText(d_floor.get_Floor_Time());
        holder.floorContent.setText(d_floor.get_Floor_Content());
        String Floornumber_string = d_floor.get_Floor_Number()+"楼";
        holder.floorNumber.setText(Floornumber_string);
    }*/


    @Override
    public int getItemCount() {
        int count = (mFloorList == null ? 0 : mFloorList.size());
        if (VIEW_FOOTER != null) {
            count++;
        }

        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            if (haveHeaderView()) position--;
            D_Floor d_floor = mFloorList.get(position);
            UserInfo userInfo = d_floor.getUserInfo();
            Picasso.with(mContext).load(userInfo.getAvatarFile()).placeholder(R.drawable.user_image).into(holder.floorHeadimage);
           // holder.floorHeadimage.setImageResource(d_floor.get_Floor_HeadId());
            if (TextUtils.isEmpty(userInfo.getNickname())){
                holder.floorName.setText(userInfo.getUserName());
            }else {
                holder.floorName.setText(userInfo.getNickname());
            }
           // holder.floorName.setText();
            holder.floorTime.setText(d_floor.get_Floor_Time());
            holder.floorContent.setText(d_floor.get_Floor_Content());
            String Floornumber_string = d_floor.get_Floor_Number()+"楼";
            holder.floorNumber.setText(Floornumber_string);
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        try {
            if (mRecyclerView == null && mRecyclerView != recyclerView) {
                mRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View getLayout(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    public void addHeaderView(View headerView, Dynamic dynamic,String praiseText,boolean isChecked) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//待更新WRAP_CONTENT
            final ImageView userImage = (ImageView)headerView.findViewById(R.id.share_user_image);
            final TextView userName = (TextView)headerView.findViewById(R.id.share_user_name);
            TextView time = (TextView)headerView.findViewById(R.id.share_time);
            TextView content = (TextView)headerView.findViewById(R.id.share_content);
            NineGridView nineGridView = (NineGridView)headerView.findViewById(R.id.nine_gridview);
            View line = headerView.findViewById(R.id.share_line);
            CheckBox box = (CheckBox)headerView.findViewById(R.id.share_favorite_checkbox);
            ImageButton comment = (ImageButton)headerView.findViewById(R.id.share_comment_button);
            TextView praise = (TextView)headerView.findViewById(R.id.share_favorite_text);
            JMessageClient.getUserInfo(dynamic.getUserInfo(), new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0){
                        Picasso.with(mContext).load(userInfo.getAvatarFile()).placeholder(R.drawable.user_image).into(userImage);
                        if (TextUtils.isEmpty(userInfo.getNickname())){
                            userName.setText(userInfo.getUserName());
                        }else {
                            userName.setText(userInfo.getNickname());
                        }
                    }
                }
            });
            List<ImageInfo>  imageInfos = new ArrayList<>();
            for (String url:dynamic.getDynamic_images()){
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl(url);
                imageInfo.setBigImageUrl(url);
                imageInfos.add(imageInfo);
            }
            nineGridView.setAdapter(new NineGridViewClickAdapter(mContext,imageInfos));
             time.setText(dynamic.getDynamic_time());
            content.setText(dynamic.getDynamic_content());
            box.setChecked(isChecked);
            praise.setText(praiseText);
            headerView.setLayoutParams(params);//九宫格
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    private void ifGridLayoutManager() {
        if (mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() :
                            1;
                }
            });
        }
    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }


    /*
    public static class D_FloorHolder extends RecyclerView.ViewHolder {

        public D_FloorHolder(View itemView) {
            super(itemView);
        }
    }*/
}
