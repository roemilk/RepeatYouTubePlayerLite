package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;
import com.venuskimblessing.youtuberepeatlite.R;

public class DialogPickerCount extends Dialog implements View.OnTouchListener {
    public static final String TAG = "DialogPickerCount";

    private Context mContext;
    private NumberPicker mNumberPicker = null;
    private OnSelectedNumberPickerListener mListener = null;
    private GestureDetector mGestureDetector = null;

    public interface OnSelectedNumberPickerListener {
        public void onSelectedValue(int value);
    }

    public DialogPickerCount(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogPickerCount(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogPickerCount(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_numberpicker);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        mNumberPicker = (NumberPicker)findViewById(R.id.number_picker);
        mNumberPicker.setOnTouchListener(this);
    }

    public void setOnSelectedNumberPickerListener(OnSelectedNumberPickerListener listener){
        this.mListener = listener;
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
            int value = mNumberPicker.getValue();
            if(mListener != null){
                mListener.onSelectedValue(value);
                dismiss();
            }
            return super.onSingleTapUp(e);
        }
    }
}
