package com.venuskimblessing.tuberepeatfree.Timer;

import android.util.Log;

import com.venuskimblessing.tuberepeatfree.Common.CommonUserData;

import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {
    private static final String TAG = "CountDownTimer";
    public static final int MAX_COUNT = 7200; //2시간

    public int mCount = 0;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private OnCountDownTimerCallbackListener mListener = null;

    public interface OnCountDownTimerCallbackListener{
        void onTimerEnd();
        void onTimerCount(int count);
    }

    public CountDownTimer() { }

    public void startTimer(int maxCount, OnCountDownTimerCallbackListener listener){
        this.mListener = listener;
        mCount = maxCount;
        if(mTimer != null){
            return;
        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "timer count : " + mCount);
                if(mCount <= 0){
//                    CommonUserData.sRemoveAllAd = false;
                    mTimerTask.cancel();
                    mTimer.cancel();
                    mCount = 0;
                    mTimer = null;
                    mTimerTask = null;

                    if(mListener != null){
                        mListener.onTimerEnd();
                    }
                }else{
                    mCount--;
                    mListener.onTimerCount(mCount);
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    public void stopTimer(){
        if(mTimerTask != null){
            mTimerTask.cancel();
        }
    }
}
