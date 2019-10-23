package com.example.friendbook.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.friendbook.R;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class EditActivity extends BaseActivity implements View.OnClickListener{
    private EditText editText_2;
    private ImageView picture1;
    private ImageView picture2;
    private ImageView picture3;
    private ImageView picture4;
    private List<String> imagePaths = new ArrayList<>();
    private int numberofpicture=1;
    public static final int CHOOSE_PHOTO = 2;
    private AVUser user;
    private EditActivity editActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();
        editActivity = this;
        user = AVUser.getCurrentUser();
        Button button_launch = (Button) findViewById(R.id.edit_launch_button);
        Button button_photolaunch = (Button) findViewById(R.id.photo_launch_button);
        initTitle(true,true,"发布动态","",false,"");
        editText_2 = (EditText)findViewById(R.id.edit_text_2);
        picture1 = (ImageView)findViewById(R.id.launch_photo1);
        picture2 = (ImageView)findViewById(R.id.launch_photo2);
        picture3 = (ImageView)findViewById(R.id.launch_photo3);
        picture4 = (ImageView)findViewById(R.id.launch_photo4);
        button_launch.setOnClickListener(this);
        button_photolaunch.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_launch_button: //   　　点击发布文本
                String edit_text2 = editText_2.getText().toString();
                if(edit_text2.isEmpty())
                    Toast.makeText(EditActivity.this,"发布内容不能为空",Toast.LENGTH_SHORT).show();
                else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditActivity.this);
                    dialog.setTitle("发布动态");
                    dialog.setMessage("您是否确认发布该动态");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dialog.setNegativeButton("发布", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String edit_text21 = editText_2.getText().toString();
                            String edit_text11;
                            if(edit_text21.length()<100)
                                edit_text11 = edit_text21;
                            else
                                edit_text11 = edit_text21.substring(0,99);
                            SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
                            Date curDate =  new Date(System.currentTimeMillis());
                            String   str   =   formatter.format(curDate);
                            Toast.makeText(EditActivity.this,edit_text21+str,Toast.LENGTH_SHORT).show();// 待修改
                            setDynamic(edit_text11,edit_text21,user,imagePaths);

                        }
                    });
                    dialog.show();
                    //finish();
                }
                break;
            case R.id.photo_launch_button:
                if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(EditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum();
                }
                break;

            default:
                break;
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openAlbum();
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if(numberofpicture==1)
            {
                picture1.setImageBitmap(bitmap);
                if(imagePaths.size()==0)
                {imagePaths.add(imagePath);

                }
                else
                {imagePaths.set(0,imagePath);

                }
                numberofpicture++;
            }
            else if(numberofpicture==2)
            {
                picture2.setImageBitmap(bitmap);

                if(imagePaths.size()<=1)
                {imagePaths.add(imagePath);

                }
                 else
                {imagePaths.set(1,imagePath);

                }
                numberofpicture++;
            }
            else if(numberofpicture==3)
            {
                picture3.setImageBitmap(bitmap);

                if(imagePaths.size()<=2)
                {imagePaths.add(imagePath);

                }
                else
                {imagePaths.set(2,imagePath);

                }

                numberofpicture++;
            }
            else if(numberofpicture==4)
            {
                picture4.setImageBitmap(bitmap);
                if(imagePaths.size()<=3)
                {imagePaths.add(imagePath);

                }
                else

                {imagePaths.set(3,imagePath);

                }
                numberofpicture=1;
            }
        }
        else{
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }



   /*
   private void showNinegridView(){
        List<ImageInfo>  imageInfos = new ArrayList<>();

        for (String imagePathnew:imagePaths){  //urls为任意图片url的List<String>
            Log.e("a",imagePathnew);
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(imagePathnew);
            imageInfo.setBigImageUrl(imagePathnew);
            imageInfos.add(imageInfo);
        }
        nineGridView.setAdapter(new NineGridViewClickAdapter(EditActivity.this,imageInfos));
    */

    private void setDynamic(String title, String content, AVUser user, final List<String> imagePaths){
        final AVObject dynamic = new AVObject("Dynamic");
        dynamic.put("dynamic_short_content",title);
        dynamic.put("dynamic_content", content);
        dynamic.put("praise", Arrays.asList(""));
        dynamic.put("user",user);
        dynamic.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    //Log.e(MainActivity,"动态保存成功");

                    for (String imagePath:imagePaths){
                        Log.e("f",imagePath);

                        final AVObject dynamicImages = new AVObject("DynamicImages");
                        dynamicImages.put("targetDynamic",dynamic);
                        Luban.with(EditActivity.this).load(imagePath)
                                .putGear(3).setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                //Log.e("MainActivity","开始图片压缩");
                            }

                            @Override
                            public void onSuccess(File file) {
                                try {
                                    //Log.e("MainActivity","图片压缩成功");
                                    FileInputStream stream = new FileInputStream(file);
                                    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
                                    byte[] b = new byte[1000];
                                    int n;
                                    while ((n = stream.read(b)) != -1)
                                        out.write(b, 0, n);
                                    stream.close();
                                    out.close();
                                    dynamicImages.put("image",new AVFile("test2.jpg",out.toByteArray()));
                                    dynamicImages.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            //Log.d(TAG, file.getUrl());//返回一个唯一的 Url 地址
                                            if(e== null)
                                            editActivity.finish();
                                            else
                                                Toast.makeText(EditActivity.this,"上传失败",Toast.LENGTH_SHORT).show();// 待修改
                                        }
                                    });
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("MainActivity","压缩图片出错了");
                            }
                        }).launch();

                    }

                }
            }
        });
    }
}