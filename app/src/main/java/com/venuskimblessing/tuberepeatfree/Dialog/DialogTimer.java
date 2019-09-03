package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.annotations.NotNull;
import com.ncorti.slidetoact.SlideToActView;
import com.venuskimblessing.tuberepeatfree.R;
import com.venuskimblessing.tuberepeatfree.Timer.CountDownTimer;
import com.venuskimblessing.tuberepeatfree.Timer.TimerSington;
import com.venuskimblessing.tuberepeatfree.Utils.MediaUtils;
import com.venuskimblessing.tuberepeatfree.Utils.SoftKeybordManager;

public class DialogTimer extends Dialog implements View.OnClickListener {
    private static final String TAG = "TimerDialog";

    private GridLayout mGridLayout;
    private TextView mTimeTextView, mCountTextView;
    private Button mBackspaceButton;
    private SlideToActView mSlideToActView;

    private LinearLayout mTimerParentLay, mTimerInputLay;
    private RelativeLayout mTimerResultLay;

    private SoftKeybordManager mSoftKeybordManager;
    private WindowManager.LayoutParams params;
    private float mBrightness;

    private Context mContext;
    private StringBuffer mTimeStringBuffer = new StringBuffer();
    private int mTimerSecResult = 0;



    public DialogTimer(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogTimer(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogTimer(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.layout_timer);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mTimerParentLay = (LinearLayout)findViewById(R.id.timer_parent_lay);
        mTimerInputLay = (LinearLayout)findViewById(R.id.timer_root_input_lay);
        mTimerResultLay = (RelativeLayout)findViewById(R.id.timer_root_result_lay);

        mGridLayout = (GridLayout)findViewById(R.id.timer_gridLayout);
        setGridLayoutEvent();

        mTimeTextView = (TextView)findViewById(R.id.timer_time_textView);
        mCountTextView = (TextView)findViewById(R.id.timer_count_textView);

        mBackspaceButton = (Button)findViewById(R.id.timer_backspace_button);
        mBackspaceButton.setOnClickListener(this);

        mSlideToActView = (SlideToActView)findViewById(R.id.timer_slideToActView);
        mSlideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                if(slideToActView.isCompleted()){
                    //타이머 종료
                    stopTimer();
                    unSavingMode();
                    showInputTimerLay();
                }
            }
        });
    }

    private void setGridLayoutEvent(){
        int childCount = mGridLayout.getChildCount();
        for (int i= 0; i < childCount; i++){
            final Button container = (Button) mGridLayout.getChildAt(i);
            container.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    // your click code here
                    String number = (String)container.getTag();
                    Log.d(TAG, "number : " + number);

                    if(number.equals("-1")){ //취소
                        dismiss();
                    }else if(number.equals("-2")){ //확인
                        //타이머 시작
                        resultTime();
                        showResultTimerLay();
                        startTimer();
                        Log.d(TAG, "타이머 시간을 초로 환산한 결과 : " + mTimerSecResult);
                    }else { //시간연산
                        calTime(number);
                    }
                }
            });
        }
    }

    private void resultTime(){
        if(mTimeStringBuffer != null){
            if(mTimeStringBuffer.length() > 0){
                String timeString = mTimeStringBuffer.toString();
                int hour = 0;
                int min = 0;
                if(mTimeStringBuffer.length() > 2){
                    hour = Integer.parseInt(timeString.substring(2, 3));
                    min = Integer.parseInt(timeString.substring(0, 2));

                    int minToHour = (hour * 60) * 60; //시간을 초로 환산
                    int minToSec = min * 60; //분을 초로 환산
                    mTimerSecResult = minToHour + minToSec;
                }else{
                    min = Integer.parseInt(timeString);
                    mTimerSecResult = min * 60; //분을 초로 환산
                }
            }
        }
    }

    private void calTime(String s){
        String timeString  = mTimeStringBuffer.toString() + s;
        int timeStringLength = timeString.length();

        if(timeStringLength > 3){
            return;
        }

        if(timeStringLength <= 2){
            String min = timeString;
            mTimeTextView.setText("0H " + min + "M");
        }else{
            String hour = timeString.substring(2, 3);
            String min = timeString.substring(0, 2);

            Log.d(TAG, "hour : " + hour);
            Log.d(TAG, "min : " + min);

            mTimeTextView.setText(hour + "H " + min + "M");
        }
        mTimeStringBuffer.append(s);
    }

    private void deleteTime(){
        int stringBufferLength = mTimeStringBuffer.length();
        if(stringBufferLength > 0){
            if(stringBufferLength >= 3){
                mTimeStringBuffer.deleteCharAt(stringBufferLength - 1);
            }else{
                mTimeStringBuffer.deleteCharAt(0);
            }

            if(mTimeStringBuffer.length() == 0){
                mTimeTextView.setText("0H " + "00M");
            }else{
                String min = mTimeStringBuffer.toString();
                mTimeTextView.setText("0H " + min + "M");
            }
        }
    }

    @Override
    public void onClick(View v) {
        deleteTime();
    }

    private void showInputTimerLay(){
        mTimerResultLay.setVisibility(View.GONE);
        mTimerInputLay.setVisibility(View.VISIBLE);
    }

    private void showResultTimerLay(){
        mTimerInputLay.setVisibility(View.GONE);
        mTimerResultLay.setVisibility(View.VISIBLE);
    }

    private void savingMode(){
        params = getWindow().getAttributes();
        mBrightness = params.screenBrightness;
        params.screenBrightness = 0.1f;
        getWindow().setAttributes(params);

        mSoftKeybordManager = new SoftKeybordManager(getWindow());
        mSoftKeybordManager.hideSystemUI();

        mTimerParentLay.setBackgroundColor(Color.TRANSPARENT);
    }

    private void unSavingMode(){
        params.screenBrightness = mBrightness;
        getWindow().setAttributes(params);
    }

    private void startTimer(){
        savingMode();
        TimerSington.getCountDownTimerInstance().startTimer(mTimerSecResult, new CountDownTimer.OnCountDownTimerCallbackListener() {
            @Override
            public void onTimerEnd() {
                //앱 종료
                Toast.makeText(mContext, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTimerCount(int count) {
                String hms = MediaUtils.getSecToHMS(count);
                mCountTextView.setText(hms);
            }
        });
    }

    private void stopTimer(){
        TimerSington.getCountDownTimerInstance().stopTimer();
    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCountTextView.getVisibility() == View.VISIBLE) {
                            mCountTextView.setVisibility(View.INVISIBLE);
                        } else {
                            mCountTextView.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
