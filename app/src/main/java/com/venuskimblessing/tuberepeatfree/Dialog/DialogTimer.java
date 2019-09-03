package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.venuskimblessing.tuberepeatfree.R;

public class DialogTimer extends Dialog implements View.OnClickListener {
    private static final String TAG = "TimerDialog";

    private GridLayout mGridLayout;
    private TextView mTimeTextView;
    private Button mBackspaceButton;

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

        mGridLayout = (GridLayout)findViewById(R.id.timer_gridLayout);
        setGridLayoutEvent();

        mTimeTextView = (TextView)findViewById(R.id.timer_time_textView);
        mBackspaceButton = (Button)findViewById(R.id.timer_backspace_button);
        mBackspaceButton.setOnClickListener(this);
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
}
