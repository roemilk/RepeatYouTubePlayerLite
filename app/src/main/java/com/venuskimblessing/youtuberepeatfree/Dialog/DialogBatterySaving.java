package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ncorti.slidetoact.SlideToActView;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

import org.jetbrains.annotations.NotNull;

public class DialogBatterySaving extends Dialog {
    public static final String TAG = "DialogPickerCount";

    private Context mContext;
    private SoftKeybordManager mSoftKeybordManager;
    private SlideToActView mSlideToActView;
    private TextView mTitleTextView, mTimeTextView;

    private WindowManager.LayoutParams params;
    private float mBrightness;

    public DialogBatterySaving(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogBatterySaving(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogBatterySaving(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_batterysaving);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        params = getWindow().getAttributes();
        mBrightness = params.screenBrightness;
        params.screenBrightness = 0.1f;
        getWindow().setAttributes(params);

        mSoftKeybordManager = new SoftKeybordManager(getWindow());
        mSoftKeybordManager.hideSystemUI();

        mTitleTextView = (TextView)findViewById(R.id.battery_title_textView);
        mTitleTextView.setSelected(true);
        mTimeTextView = (TextView)findViewById(R.id.battery_time_textView);
        mSlideToActView = (SlideToActView)findViewById(R.id.battery_slideToActView);
        mSlideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                if(slideToActView.isCompleted()){
                    Toast.makeText(mContext, "베터리 절약 모드가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        params.screenBrightness = mBrightness;
        getWindow().setAttributes(params);
    }

    public void setTitle(String title){
        this.mTitleTextView.setText(title);
    }

    public void setTime(String time){
        this.mTimeTextView.setText(time);
    }
}
