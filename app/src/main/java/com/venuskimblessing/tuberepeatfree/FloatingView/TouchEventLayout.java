package com.venuskimblessing.tuberepeatfree.FloatingView;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by admin170512 on 2018-08-27.
 */

public class TouchEventLayout extends LinearLayout {
    private Context mContext;
    private OnTouchListener mListener;

    public TouchEventLayout(Context context, OnTouchListener mListener) {
        super(context);
        this.mContext = context;
        this.mListener = mListener;
    }

    public TouchEventLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mListener.onTouch(getRootView(), ev);
    }
}
