package com.example.friendbook.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.friendbook.R;
import com.example.friendbook.Util.NativeImageLoader;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.friendbook.activity.EditActivity.CHOOSE_PHOTO;

/**
 * Created by cebrandy on 2017/12/6.
 */

public class gerenziliao extends AppCompatActivity {
    List<String> strs = new ArrayList<String>();
    ArrayAdapter adapter;
   // private UserInfo userInfo;
    private CircleImageView user_touxiang;
    private gerenziliao gerenziliao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenziliao_cebian);
        //userInfo = JMessageClient.getMyInfo();
        gerenziliao = this;
        init();
        ListView lv = (ListView) findViewById(R.id.listview_perinfo);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strs);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(gerenziliao.this, (String) adapter.getItem(i), Toast.LENGTH_SHORT).show();
            }
        });

       user_touxiang = (CircleImageView) findViewById(R.id.my_edit_image);

        Picasso.with(gerenziliao.this).load(JMessageClient.getMyInfo().getAvatarFile()).placeholder(R.drawable.user_logo).into(user_touxiang);
        user_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(gerenziliao.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(gerenziliao.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOOSE_PHOTO);
                }

            }
        });
}

    void init() {
        {
                        strs.clear();
                        String nickname=JMessageClient.getMyInfo().getNickname();
                        UserInfo.Gender gender = JMessageClient.getMyInfo().getGender();
                        String sex;
            if (gender.equals(UserInfo.Gender.male)){
                sex = "男";
            }else if (gender.equals(UserInfo.Gender.female)){
                sex="女";
            }else {
                sex="未知";
            }
                        String address = JMessageClient.getMyInfo().getAddress();
            String sig = JMessageClient.getMyInfo().getSignature();
                        if (nickname!=null)
                        {
                            strs.add("昵称： " + nickname);
                        }
                        else
                        {
                            strs.add("昵称： "+"未设置昵称");
                        }
            if (sex.length()!=0)
            {
                strs.add("性别： " + sex);
            }
            else
            {
                strs.add("性别： "+"未设置性别");
            }
            if (address.length()!=0)
            {
                strs.add("地址： " + address);
            }
            else
            {
                strs.add("地址： "+"未设置地址");
            }


        if (sig.length()!=0)
        {
            strs.add("签名： " + sig);
        }
        else
        {
            strs.add("签名： "+"未设置签名");
        }
    }

                }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOOSE_PHOTO);
                }
                else
                {
                    Toast.makeText(this,"你取消了授权",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19)
                    {
                        handleleImageOnKitKat(data);
                    }
                    else
                    {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void edit(View v) {
        startActivity(new Intent(gerenziliao.this,gerenziliaoEditor.class));
    }

    @TargetApi(19)
    private void handleleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri =data.getData();
        if(DocumentsContract.isDocumentUri(this,uri))
        {
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority()))
            {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority()))
            {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection)
    {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }
    private void displayImage(String imagePath){

        if(imagePath!=null)
        {
            final Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            File file = saveBitmapFile(bitmap);
            JMessageClient.updateUserAvatar(file, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if(i==0){
                        //Toast.makeText(gerenziliao,"获取图片CG",Toast.LENGTH_SHORT).show();
                        user_touxiang .setImageBitmap(bitmap);
                    }else {
                        Toast.makeText(gerenziliao,"获取图片GG"+s,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }
    public void back(View v) {
        finish();
    }
    public File saveBitmapFile(Bitmap bitmap)

    {
        File file=new File(Environment.getExternalStorageDirectory(),"1.jpg");//将要保存图片的路径
        try{
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return file;
    }
}

