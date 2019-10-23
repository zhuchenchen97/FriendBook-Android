package com.example.friendbook.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;
import com.example.friendbook.R;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static cn.jpush.im.android.api.model.UserInfo.Field.gender;
import static cn.jpush.im.android.api.model.UserInfo.Gender.female;
import static cn.jpush.im.android.api.model.UserInfo.Gender.male;

public class gerenziliaoEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenziliaoeditor);

        Button button_add = (Button) findViewById(R.id.button_perinfo_update);
        final String nickname = JMessageClient.getMyInfo().getNickname();
        final String sig = JMessageClient.getMyInfo().getSignature();
        UserInfo.Gender gender = JMessageClient.getMyInfo().getGender();
        String sex;
        if (gender.equals(male)){
            sex ="男";
        }else if (gender.equals(female)){
            sex="女";
        }else {
            sex="未知";
        }
        final String address = JMessageClient.getMyInfo().getAddress();

        ((EditText) findViewById(R.id.per_info_name_et)).setText(nickname);
        ((EditText) findViewById(R.id.per_info_sex_et)).setText(sex);
        ((EditText) findViewById(R.id.per_info_add_et)).setText(address);
        ((EditText) findViewById(R.id.per_info_sig_et)).setText(sig);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = ((EditText) findViewById(R.id.per_info_name_et)).getText().toString();
                String sex = ((EditText) findViewById(R.id.per_info_sex_et)).getText().toString();
                UserInfo.Gender gender;
                if (sex.equals("男")){
                     gender = male;
                }else if (sex.equals("女")){
                    gender = female;
                }else {
                    gender = UserInfo.Gender.unknown;
                }
                String add = ((EditText) findViewById(R.id.per_info_add_et)).getText().toString();
                String sig = ((EditText) findViewById(R.id.per_info_sig_et)).getText().toString();
                UserInfo myUserInfo = JMessageClient.getMyInfo();
                myUserInfo.setNickname(nickname);
                myUserInfo.setBirthday(System.currentTimeMillis());
                myUserInfo.setSignature(sig);
                myUserInfo.setAddress(add);
                myUserInfo.setGender(gender);
                JMessageClient.updateMyInfo(UserInfo.Field.all,myUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                            if (i==0) {
                                Toast.makeText(gerenziliaoEditor.this, "提交成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(gerenziliaoEditor.this,"提交失败",Toast.LENGTH_SHORT).show();
                            }
                    }
                });


            }
        });
    }

    public void back_perinfo(View v){finish();}
}
