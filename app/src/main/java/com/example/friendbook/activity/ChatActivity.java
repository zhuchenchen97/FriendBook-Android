package com.example.friendbook.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;

import com.example.friendbook.Application.JGApplication;
import com.example.friendbook.R;
import com.example.friendbook.Util.SimpleCommonUtils;
import com.example.friendbook.Util.keyboard.XhsEmoticonsKeyBoard;
import com.example.friendbook.Util.keyboard.data.EmoticonEntity;
import com.example.friendbook.Util.keyboard.interfaces.EmoticonClickListener;
import com.example.friendbook.adapter.ChattingListAdapter;
import com.example.friendbook.entity.Event;
import com.example.friendbook.entity.EventType;
//import com.example.friendbook.location.activity.MapPickerActivity;
import com.example.friendbook.module.Constants;

import com.example.friendbook.Util.ToastUtil;
import com.example.friendbook.Util.ImageEvent;
//import com.example.friendbook.Util.imagepicker.bean.ImageItem;
import com.example.friendbook.Util.keyboard.utils.EmoticonsKeyboardUtils;
import com.example.friendbook.Util.keyboard.widget.EmoticonsEditText;
import com.example.friendbook.Util.keyboard.widget.FuncLayout;
//import com.example.friendbook.Util.photovideo.takevideo.CameraActivity;
import com.example.friendbook.view.ChatActivityView;
import com.example.friendbook.view.SimpleAppsGridView;
import com.example.friendbook.view.listview.DropDownListView;


/**
 * Created by ${chenyn} on 2017/3/26.
 */

public class ChatActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener, View.OnClickListener {
    @BindView(R.id.lv_chat)
    DropDownListView lvChat;
    @BindView(R.id.ek_bar)
    XhsEmoticonsKeyBoard ekBar;
    public static final String JPG = ".jpg";
    private static String MsgIDs = "msgIDs";

    private String mTitle;
    private boolean mLongClick = false;

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    private static final String DRAFT = "draft";
    //private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    public static final int REQUEST_CODE_SELECT = 100;
    private ChatActivityView mChatView;
    private boolean mIsSingle = true;
    private Conversation mConv;
    private String mTargetId;
    private String mTargetAppKey;
    private Activity mContext;
    private ChattingListAdapter mChatAdapter;
    int maxImgCount = 9;
    private List<UserInfo> mAtList;
    private static final int REFRESH_LAST_PAGE = 0x1023;
    private static final int REFRESH_CHAT_TITLE = 0x1024;
    private Dialog mDialog;

    private UserInfo mMyInfo;
    private boolean mShowSoftInput = false;
    private List<UserInfo> forDel = new ArrayList<>();

    Window mWindow;
    InputMethodManager mImm;
    private final UIHandler mUIHandler = new UIHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_chat);
        mChatView = (ChatActivityView) findViewById(R.id.chat_view);
        mChatView.initModule(mDensity, mDensityDpi);
        this.getSupportActionBar().hide();
        this.mWindow = getWindow();
        this.mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatView.setListeners(this);

        ButterKnife.bind(this);

        initData();
        initView();

    }

    private void initData() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        Intent intent = getIntent();
        mTargetId = intent.getStringExtra(TARGET_ID);
        mTargetAppKey = intent.getStringExtra(TARGET_APP_KEY);
        mTitle = intent.getStringExtra(JGApplication.CONV_TITLE);
        mMyInfo = JMessageClient.getMyInfo();
        if (!TextUtils.isEmpty(mTargetId)) {
            //单聊
            mIsSingle = true;
            mChatView.setChatTitle(mTitle);
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            if (mConv == null) {
                mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
            }
            mChatAdapter = new ChattingListAdapter(mContext, mConv);
        }

        String draft = intent.getStringExtra(DRAFT);
        if (draft != null && !TextUtils.isEmpty(draft)) {
            ekBar.getEtChat().setText(draft);
        }

        mChatView.setChatListAdapter(mChatAdapter);
//        mChatAdapter.initMediaPlayer();
        mChatView.getListView().setOnDropDownListener(new DropDownListView.OnDropDownListener() {
            @Override
            public void onDropDown() {
                mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000);
            }
        });
        mChatView.setToBottom();
        mChatView.setConversation(mConv);
    }

    private void initView() {
        initEmoticonsKeyBoardBar();
        initListView();
    }

    private void initEmoticonsKeyBoardBar() {
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        SimpleAppsGridView gridView = new SimpleAppsGridView(this);
        ekBar.addFuncView(gridView);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        //发送按钮
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcgContent = ekBar.getEtChat().getText().toString();
                scrollToBottom();
                if (mcgContent.equals("")) {
                    return;
                }
                Message msg;
                TextContent content = new TextContent(mcgContent);

                    msg = mConv.createSendMessage(content);

                mChatAdapter.addMsgToList(msg);
                JMessageClient.sendMessage(msg);
                ekBar.getEtChat().setText("");
                if (mAtList != null) {
                    mAtList.clear();
                }
                if (forDel != null) {
                    forDel.clear();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jmui_return_btn:
                returnBtn();
                break;
            /*
            case R.id.jmui_right_btn:
                startChatDetailActivity(mTargetId, mTargetAppKey, mGroupId);
                break;*/
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnBtn();
    }
    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage(((EmoticonEntity) o).getIconUri());
                    }
                } else {
                    String content = null;
                    if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };
    private void returnBtn() {
        mConv.resetUnreadCount();
        dismissSoftInput();
        JMessageClient.exitConversation();
        //发送保存为草稿事件到会话列表
        EventBus.getDefault().post(new Event.Builder().setType(EventType.draft)
                .setConversation(mConv)
                .setDraft(ekBar.getEtChat().getText().toString())
                .build());
        finish();
    }

    private void dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(ekBar.getEtChat().getWindowToken(), 0);
                mShowSoftInput = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startChatDetailActivity(String targetId, String appKey, long groupId) {
        Intent intent = new Intent();
        intent.putExtra(TARGET_ID, targetId);
        intent.putExtra(TARGET_APP_KEY, appKey);
        //intent.setClass(this, ChatDetailActivity.class);
        startActivityForResult(intent, JGApplication.REQUEST_CODE_CHAT_DETAIL);
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void initListView() {
        lvChat.setAdapter(mChatAdapter);
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ekBar.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private void scrollToBottom() {
        lvChat.requestLayout();
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                lvChat.setSelection(lvChat.getBottom());
            }
        });
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
        ekBar.reset();
    }

    @Override
    protected void onResume() {
        if (JGApplication.addForwardMsg != null && JGApplication.addForwardMsg.size() > 0) {
            mChatAdapter.addMsgToList(JGApplication.addForwardMsg.get(0));
        }
        mChatAdapter.notifyDataSetChanged();
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (null != targetId) {
            String appKey = getIntent().getStringExtra(TARGET_APP_KEY);
            JMessageClient.enterSingleConversation(targetId, appKey);
        }
        super.onResume();

    }

    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getTargetType() == ConversationType.single) {
                    UserInfo userInfo = (UserInfo) message.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    String appKey = userInfo.getAppKey();
                    if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                        Message lastMsg = mChatAdapter.getLastMsg();
                        if (lastMsg == null || message.getId() != lastMsg.getId()) {
                            mChatAdapter.addMsgToList(message);
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void onEvent(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        mChatAdapter.delMsgRetract(retractedMessage);
    }


/*
    public void onEventMainThread(ImageEvent event) {
        Intent intent;
        switch (event.getFlag()) {
            case JGApplication.IMAGE_MESSAGE:
                int from = PickImageActivity.FROM_LOCAL;
                int requestCode = RequestCode.PICK_IMAGE;
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                } else {
                    PickImageActivity.start(ChatActivity.this, requestCode, from, tempFile(), true, 9,
                            true, false, 0, 0);
                }
                break;
            default:
                break;
        }

    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {

            case JGApplication.RESULT_CODE_CHAT_DETAIL:
                String title = data.getStringExtra(JGApplication.CONV_TITLE);
                mChatView.setChatTitle(title);
                if (data.getBooleanExtra("deleteMsg", false)) {
                    mChatAdapter.clearMsgList();
                }
                break;
        }

    }


    /**
     * 图片选取回调
     */
    /*
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            sendImageAfterSelfImagePicker(data);
        }
    }
*/
    /**
     * 发送图片
     */
/*
    private void sendImageAfterSelfImagePicker(final Intent data) {
        SendImageHelper.sendImageAfterSelfImagePicker(this, data, new SendImageHelper.Callback() {
            @Override
            public void sendImage(final File file, boolean isOrig) {

                //所有图片都在这里拿到
                ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                        if (responseCode == 0) {
                            Message msg = mConv.createSendMessage(imageContent);
                            handleSendMsg(msg.getId());
                        }
                    }
                });

            }
        });
    }
*/
    //发送极光熊
    private void OnSendImage(String iconUri) {
        String substring = iconUri.substring(7);
        File file = new File(substring);
        ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                if (responseCode == 0) {
                    imageContent.setStringExtra("jiguang", "xiong");
                    Message msg = mConv.createSendMessage(imageContent);
                    handleSendMsg(msg.getId());
                } else {
                    ToastUtil.shortToast(mContext, responseMessage);
                }
            }
        });
    }

    /**
     * 处理发送图片，刷新界面
     *
     * @param data intent
     */
    private void handleSendMsg(int data) {
        mChatAdapter.setSendMsgs(data);
        mChatView.setToBottom();
    }

    private static class UIHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;

        public UIHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_LAST_PAGE:
                        activity.mChatAdapter.dropDownToRefresh();
                        activity.mChatView.getListView().onDropDownComplete();
                        if (activity.mChatAdapter.isHasLastPage()) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                activity.mChatView.getListView()
                                        .setSelectionFromTop(activity.mChatAdapter.getOffset(),
                                                activity.mChatView.getListView().getHeaderHeight());
                            } else {
                                activity.mChatView.getListView().setSelection(activity.mChatAdapter
                                        .getOffset());
                            }
                            activity.mChatAdapter.refreshStartPosition();
                        } else {
                            activity.mChatView.getListView().setSelection(0);
                        }
                        //显示上一页的消息数18条
                        activity.mChatView.getListView()
                                .setOffset(activity.mChatAdapter.getOffset());
                        break;

                }
            }
        }
    }

}
