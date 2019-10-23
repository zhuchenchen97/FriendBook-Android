package com.example.friendbook.activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.friendbook.Util.FileHelper;
import com.example.friendbook.Util.NativeImageLoader;
import com.example.friendbook.view.NoslidingViewPager;
import com.example.friendbook.R;
import com.example.friendbook.fragment.BookshelfFragment;
import com.example.friendbook.fragment.ChatFragment;
import com.example.friendbook.fragment.ShareFragment;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NoslidingViewPager viewPager;
    private PopupWindow popupWindow;
    private ImageButton addbook;
    private ImageButton book_list;
    private ListView filter_list;
    private Toolbar toolbar;
    private ImageButton addfriend;
    private String filter = "全部动态";
    private FloatingActionButton fab;
    private UserInfo userInfo;
    private TextView toolbar_text;
    private ImageButton friend_list;
    private List<Fragment> fragments = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener listener =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share:
                    viewPager.setCurrentItem(0);
                    fab.setVisibility(View.VISIBLE);
                    addfriend.setVisibility(View.INVISIBLE);
                    friend_list.setVisibility(View.INVISIBLE);
                    //share_filter.setVisibility(View.VISIBLE);
                    addbook.setVisibility(View.GONE);
                    book_list.setVisibility(View.GONE);
                    toolbar_text.setText(filter);
                    return true;
                case R.id.bookshelf:
                    viewPager.setCurrentItem(1);
                    addfriend.setVisibility(View.INVISIBLE);
                    friend_list.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    addbook.setVisibility(View.VISIBLE);
                    book_list.setVisibility(View.VISIBLE);
                   // share_filter.setVisibility(View.INVISIBLE);
                    toolbar_text.setText("我的书架");
                    return true;
                case R.id.chat:
                    viewPager.setCurrentItem(3);
                    addfriend.setVisibility(View.VISIBLE);
                    friend_list.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    addbook.setVisibility(View.GONE);
                    book_list.setVisibility(View.GONE);
                   // share_filter.setVisibility(View.INVISIBLE);
                    toolbar_text.setText("聊天");
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userInfo = JMessageClient.getMyInfo();

        NineGridView.setImageLoader(new PicassoImageLoader());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addbook = (ImageButton) findViewById(R.id.add_new_book);
        book_list = (ImageButton) findViewById(R.id.book_list);
        addfriend = (ImageButton)toolbar.findViewById(R.id.add_new_friend);
        friend_list = (ImageButton)toolbar.findViewById(R.id.friend_list);
        toolbar_text = (TextView)toolbar.findViewById(R.id.toolbar_text);
        //share_filter = (ImageButton)toolbar.findViewById(R.id.share_filter);
        /*share_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupwindow();
            }
        });*/

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"请进行发布动态的操作",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });
        friend_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FriendListActivity.class);
                startActivity(intent);
            }
        });
        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"添加书到书架的操作",Toast.LENGTH_SHORT).show();
            }
        });
        book_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"进入总的图书列表的操作",Toast.LENGTH_SHORT).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headView = navigationView.getHeaderView(0);
        CircleImageView user_touxiang = (CircleImageView)headView.findViewById(R.id.profile_image);

        Picasso.with(MainActivity.this).load(userInfo.getAvatarFile()).error(R.drawable.user_logo).into(user_touxiang);
        user_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0){
                            NativeImageLoader.getInstance().updateBitmapFromCache(userInfo.getUserName(), bitmap);
                            Intent intent = new Intent(MainActivity.this, BigPictureActivity.class);
                            intent.putExtra("avatarPath", userInfo.getUserName());
                            startActivity(intent);
                        }
                    }
                });



            }
        });
        TextView user_nick = (TextView)headView.findViewById(R.id.nav_user_nick);
        user_nick.setText(userInfo.getNickname());
        TextView user_sign = (TextView)headView.findViewById(R.id.nav_user_sign);
        user_sign.setText(userInfo.getSignature());


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        fragments.add(ShareFragment.newInstance());
        fragments.add(BookshelfFragment.newInstance());
        fragments.add(ChatFragment.newInstance());

        viewPager = (NoslidingViewPager)findViewById(R.id.nosliding_viewpager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);//预加载剩下两页
    }

    /**
     * 显示popupWindow
     */
    private void showPopupwindow(){
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_layout,null);
        final List<String> filters =new ArrayList<String>();
        filters.add("全部动态");
        filters.add("我的");
        filters.add("好友");
        filters.add("附近");
        filter_list = (ListView)contentView.findViewById(R.id.list_filter);
        filter_list.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,filters));
        //为ListView设置点击事件
        filter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filter = filters.get(position);
                popupWindow.dismiss();
                toolbar_text.setText(filter);
            }
        });
        //显示popupWindow
        popupWindow = new PopupWindow(contentView );
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(300);
        //设置外部是否可以点击
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //这句要放在最后，否则前面的效果不会实现。
        popupWindow.showAsDropDown(toolbar_text,-20,50);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_userInformation) {
            Intent intent = new Intent(MainActivity.this,gerenziliao.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_collection ){
            Intent intent = new Intent(MainActivity.this,FriendListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_set) {
            Intent intent = new Intent(MainActivity.this,FriendListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_user) {
            Intent intent = new Intent(MainActivity.this,userActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /** Picasso 加载 */
    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Picasso.with(context).load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }


    @Override
    public void onDestroy() {
        JMessageClient.logout();
        AVUser.logOut();
        super.onDestroy();
    }
}
