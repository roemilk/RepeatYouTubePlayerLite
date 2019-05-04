package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;

public class DialogPickerTime extends Dialog implements View.OnTouchListener, View.OnClickListener {
    public static final String TAG = "DialogPickerCount";

    public static final int TYPE_START_TIME = 0;
    public static final int TYPE_END_TIME = 1;

    private Context mContext;
    private TextView mTitleTextView;
    private NumberPicker mPickerHour, mPickerMin, mPickerSec = null;
    private Button mConfirmButton, mCancelButton;
    private OnSelectedNumberPickTimeListener mListener = null;
    private GestureDetector mGestureDetector = null;
    private Resources mResources;

    private int mHour, mMin, mSec = 0;
    private int mDuration = 0;
    private int mType = 0;

    public interface OnSelectedNumberPickTimeListener{
        public void onSelectedTimeResult(int type, int duration);
    }

    public DialogPickerTime(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogPickerTime(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogPickerTime(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_timepicker);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        try{
            mResources = mContext.getResources();
        }catch(Exception e){
            mResources = null;
        }


        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mTitleTextView = (TextView)findViewById(R.id.dialog_timePicker_textView);
        mPickerHour = (NumberPicker) findViewById(R.id.dialog_timePicker_hour);
        mPickerMin = (NumberPicker)findViewById(R.id.dialog_timePicker_min);
        mPickerSec = (NumberPicker)findViewById(R.id.dialog_timePicker_sec);
        mConfirmButton = (Button)findViewById(R.id.dialog_timePicker_confirm_button);
        mCancelButton = (Button)findViewById(R.id.dialog_timePicker_cancel_button);

        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    public void setOnSelectedNumberPickerListener(OnSelectedNumberPickTimeListener listener){
        this.mListener = listener;
    }

    public void setType(int type){
        this.mType = type;
        switch (type){
            case TYPE_START_TIME:
                if(mResources != null){
                    mTitleTextView.setText(mResources.getString(R.string.player_repeat_starttime_picker));
                }else{
                    mTitleTextView.setText("Please select a start time.");
                }
                break;
            case TYPE_END_TIME:
                if(mResources != null){
                    mTitleTextView.setText(mResources.getString(R.string.player_repeat_startendtime_picker));
                }else{
                    mTitleTextView.setText("Please select a time to finish.");
                }
                break;
        }
    }

    public void setData(int duration, int currentTime){
        this.mDuration = duration;
        String result = MediaUtils.getMillSecToHMS(currentTime);
        String[] splitArr = result.split(":");

        int hour = Integer.parseInt(splitArr[0]);
        int min = Integer.parseInt(splitArr[1]);
        int sec = Integer.parseInt(splitArr[2]);
        mPickerHour.setValue(hour);
        mPickerMin.setValue(min);
        mPickerSec.setValue(sec);
    }

    /**
     * 설정한 시간이 최대 또는 최소 Duration을 넘는지 체크합니다.
     * @return
     */
    private boolean checkOverTime(String isoTime){
        int time = (int)MediaUtils.getDuration(isoTime);

        if(time > mDuration || time <= 0){
            Toast.makeText(mContext, "설정한 시간이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private String convertISOTime(){
        mHour = mPickerHour.getValue();
        mMin = mPickerMin.getValue();
        mSec = mPickerSec.getValue();

        StringBuffer sb = new StringBuffer();
        sb.append("PT");
        if(mHour > 0){
            sb.append(mHour + "H");
        }

        if(mMin > 0){
            sb.append(mMin + "M");
        }

        if(mSec > 0){
            sb.append(mSec + "S");
        }

        String result = sb.toString().trim();
        Log.d(TAG, "result : " + result);

        return result;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    //NumberPicker GestureListener
    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_timePicker_confirm_button:
                String convertResult = convertISOTime();
                boolean checkState = checkOverTime(convertResult);
                if(checkState){
                    int duration = (int)MediaUtils.getDuration(convertResult);
                    mListener.onSelectedTimeResult(mType, duration);
                    dismiss();
                }
                break;

            case R.id.dialog_timePicker_cancel_button:
                dismiss();
                break;
        }
    }
}
