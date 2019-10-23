package com.example.friendbook.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 11972 on 2017/10/3.
 */

public class NoslidingViewPager extends ViewPager {
    public NoslidingViewPager(Context context) {
        super(context);
    }

    public NoslidingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * 啥都不做
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 将滑动事件传递给下一个按钮
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
