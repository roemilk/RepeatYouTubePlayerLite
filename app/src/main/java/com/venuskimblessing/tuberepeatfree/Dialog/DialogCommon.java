package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.venuskimblessing.tuberepeatfree.R;

public class DialogCommon extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogCommon";

    private Context mContext;
    private RelativeLayout mParentLay;
    private LinearLayout mBackgroundLay, mButtonLay;
    private ImageView mLogoImageView;
    private TextView mTitleTextView, mContentTextView;
    public Button mOneButton, mTwoButton, mThreeButton;

    private View.OnClickListener listener = null;

    public DialogCommon(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogCommon(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogCommon(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_common);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mParentLay = (RelativeLayout)findViewById(R.id.dialog_common_parent_lay);
        mLogoImageView = (ImageView)findViewById(R.id.dialog_common_logo_imageView);
        mTitleTextView = (TextView)findViewById(R.id.dialog_common_title_textView);
        mContentTextView = (TextView)findViewById(R.id.dialog_common_content_textView);
        mOneButton = (Button)findViewById(R.id.dialog_common_one_button);
        mTwoButton = (Button)findViewById(R.id.dialog_common_two_button);
        mThreeButton = (Button)findViewById(R.id.dialog_common_three_button);
        mButtonLay = (LinearLayout)findViewById(R.id.dialog_common_button_lay);
        mBackgroundLay = (LinearLayout)findViewById(R.id.dialog_common_background_lay);

        mParentLay.setOnClickListener(this);
        mOneButton.setOnClickListener(this);
        mTwoButton.setOnClickListener(this);
        mThreeButton.setOnClickListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            listener = onClickListener;
        }
    }

    public void setTitle(String title){
        this.mTitleTextView.setText(title);
    }

    public void setContent(String content){
        this.mContentTextView.setText(content);
    }

    public void setBackgroundParentLay(int color){
        mBackgroundLay.setBackgroundColor(color);
    }

    public void hideButtonLay(){
        mButtonLay.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_common_parent_lay:
                dismiss();
                break;
            case R.id.dialog_common_one_button:
                listener.onClick(v);
                break;
            case R.id.dialog_common_two_button:
                listener.onClick(v);
                break;
            case R.id.dialog_common_three_button:
                listener.onClick(v);
                break;
        }
        dismiss();
    }
}
