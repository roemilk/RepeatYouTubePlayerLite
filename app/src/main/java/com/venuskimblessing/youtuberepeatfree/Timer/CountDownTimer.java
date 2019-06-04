package com.venuskimblessing.youtuberepeatfree.Timer;

import android.util.Log;

import com.venuskimblessing.youtuberepeatfree.Common.CommonUserData;

import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {
    private static final String TAG = "CountDownTimer";

    public static int mMaxCount = 30;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private OnCountDownTimerCallbackListener mListener = null;

    public interface OnCountDownTimerCallbackListener{
        void onTimerEnd();
    }

    public CountDownTimer() { }

    public void startTimer(int maxCount, OnCountDownTimerCallbackListener listener){
        this.mListener = listener;
        mMaxCount = maxCount;
        if(mTimer != null){
            return;
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "count : " + mMaxCount);
                if(mMaxCount <= 0){
                    CommonUserData.sRemoveAllAd = false;
                    mTimerTask.cancel();
                    mTimer.cancel();
                    mMaxCount = 0;
                    mTimer = null;
                    mTimerTask = null;

                    if(mListener != null){
                        mListener.onTimerEnd();
                    }
                }else{
                    mMaxCount--;
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }
}
