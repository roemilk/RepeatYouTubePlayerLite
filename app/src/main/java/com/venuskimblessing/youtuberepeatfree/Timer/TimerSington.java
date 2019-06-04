package com.venuskimblessing.youtuberepeatfree.Timer;

public class TimerSington {
    private final String TAG = "TimerSington";
    public static CountDownTimer mTimerSingleTon = null;
    public static CountDownTimer getCountDownTimerInstance(){
        if(mTimerSingleTon == null){
            mTimerSingleTon = new CountDownTimer();
            return mTimerSingleTon;
        }else{
            return mTimerSingleTon;
        }
    }
}
