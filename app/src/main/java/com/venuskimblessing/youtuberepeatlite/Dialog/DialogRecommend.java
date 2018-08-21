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
    private Button mHomeButton, mDanceButton, mAsmrButton, mMakeupButton, mMagicButton;
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

        mRootLay.setOnClickListener(this);
        mHomeButton.setOnClickListener(this);
        mDanceButton.setOnClickListener(this);
        mAsmrButton.setOnClickListener(this);
        mMakeupButton.setOnClickListener(this);
        mMagicButton.setOnClickListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            this.mListener = onClickListener;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.sort_menu_root_lay){
            dismiss();
        }else{
            if(mListener != null){
                mListener.onClick(view);
            }
        }
    }
}
