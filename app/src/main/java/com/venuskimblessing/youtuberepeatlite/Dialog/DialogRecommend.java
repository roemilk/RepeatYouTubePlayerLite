package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.venuskimblessing.youtuberepeatlite.R;

public class DialogRecommend extends Dialog implements View.OnClickListener {
    public static final String TAG = "DialogHelp";

    private Context mContext = null;
    private RelativeLayout mRootLay;
    private Button mHomeButton, mDanceButton, mAsmrButton, mMakeupButton, mMagicButton,
            mCookButton, mMusicButton, mSleepButton, mRelaxingButton, mKidsButton,
            mSexyButton, mSexyAniButton, mSexySound;
    private View.OnClickListener mListener = null;

    public DialogRecommend(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogRecommend(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogRecommend(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.layout_dialog_recommend);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mRootLay = (RelativeLayout)findViewById(R.id.recomment_menu_root_lay);
        mHomeButton = (Button)findViewById(R.id.recommend_home_button);
        mDanceButton = (Button)findViewById(R.id.recommend_dance_button);
        mAsmrButton = (Button)findViewById(R.id.recommend_asmr_button);
        mMakeupButton = (Button)findViewById(R.id.recommend_makeup_button);
        mMagicButton = (Button)findViewById(R.id.recommend_magic_button);

        mCookButton = (Button)findViewById(R.id.recommend_cook_button);
        mMusicButton = (Button)findViewById(R.id.recommend_billbordMusic_button);
        mSleepButton = (Button)findViewById(R.id.recommend_sleep_button);
        mRelaxingButton = (Button) findViewById(R.id.recommend_relaxing_button);
        mKidsButton = (Button) findViewById(R.id.recommend_kids_button);

        mSexyButton = (Button) findViewById(R.id.recommend_sexy_button);
        mSexyAniButton = (Button) findViewById(R.id.recommend_sexyAni_button);
        mSexySound = (Button) findViewById(R.id.recommend_sexySound_button);

        mRootLay.setOnClickListener(this);
        mHomeButton.setOnClickListener(this);
        mDanceButton.setOnClickListener(this);
        mAsmrButton.setOnClickListener(this);
        mMakeupButton.setOnClickListener(this);
        mMagicButton.setOnClickListener(this);

        mCookButton.setOnClickListener(this);
        mMusicButton.setOnClickListener(this);
        mSleepButton.setOnClickListener(this);
        mRelaxingButton.setOnClickListener(this);
        mKidsButton.setOnClickListener(this);

        mSexyButton.setOnClickListener(this);
        mSexyAniButton.setOnClickListener(this);
        mSexySound.setOnClickListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            this.mListener = onClickListener;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.recomment_menu_root_lay){
            dismiss();
        }else{
            if(mListener != null){
                mListener.onClick(view);
            }
        }
    }
}
