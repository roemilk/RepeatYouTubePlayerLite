package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.venuskimblessing.youtuberepeatfree.R;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingIndicator extends Dialog {

    private Context mContext = null;
    private AVLoadingIndicatorView mAvi;

    public LoadingIndicator(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public LoadingIndicator(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    public LoadingIndicator(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_loading_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mAvi = (AVLoadingIndicatorView)findViewById(R.id.loading_avi);
    }

    public void startLoadingView(){
        mAvi.show();
        this.show();
    }

    public void stopLoadingView(){
        mAvi.hide();
        this.dismiss();
    }
}
