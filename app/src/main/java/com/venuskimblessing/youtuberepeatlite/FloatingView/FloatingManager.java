package com.venuskimblessing.youtuberepeatlite.FloatingView;

import android.content.Context;
import android.content.Intent;

public class FloatingManager {
    private final String TAG = "FloatingManager";

    public static final int TYPE_INTENT_DATA = 0;
    public static final int TYPE_INTENT_LIST = 1;

    private Context mContext;

    public FloatingManager(Context context) {
        this.mContext = context;
    }

    /**
     * 플로팅 뷰를 중지한다. (서비스)
     */
    public void stopOverlay(){
        mContext.stopService(new Intent(mContext, FloatingService.class));
    }
}
