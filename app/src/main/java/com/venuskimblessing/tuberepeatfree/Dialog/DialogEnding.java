package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.venuskimblessing.tuberepeatfree.R;
import com.wang.avi.AVLoadingIndicatorView;

public class DialogEnding extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogEnding";

    private Context mContext;
//    private AdView mAdView;
    private Button mYesButton, mNoButton;
    private TextView mEndingDialogTextView;
    private LinearLayout mEndingDialogLoadingLay;
    private AVLoadingIndicatorView mLoadingIndicator;

    public DialogEnding(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogEnding(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogEnding(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
        showBanner();
    }

    private void init() {
        setContentView(R.layout.layout_dialog_ending);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mEndingDialogLoadingLay = (LinearLayout)findViewById(R.id.ending_dialog_loading_lay);
        mEndingDialogTextView = (TextView)findViewById(R.id.dialog_ending_title_textView);
//        mLoadingIndicator = (AVLoadingIndicatorView)findViewById(R.id.ending_dialog_pacman_indicator);
//        mLoadingIndicator.setIndicatorColor(Color.DKGRAY);
//        mLoadingIndicator.show();

//        MobileAds.initialize(mContext,
//                CommonApiKey.KEY_ADMOB_APP_ID);

//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(adListener);

        mYesButton = (Button)findViewById(R.id.ending_dialog_yes_button);
        mNoButton = (Button)findViewById(R.id.ending_dialog_no_button);

        mYesButton.setOnClickListener(this);
        mNoButton.setOnClickListener(this);
    }

    private void showBanner(){
//        mLoadingIndicator.hide();
//        mEndingDialogLoadingLay.setVisibility(View.GONE);
//        mAdView.setVisibility(View.VISIBLE);
        mEndingDialogTextView.setVisibility(View.VISIBLE);
        mYesButton.setVisibility(View.VISIBLE);
        mNoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ending_dialog_yes_button:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.ending_dialog_no_button:
                dismiss();
                break;
        }
    }

//    private AdListener adListener = new AdListener() {
//        @Override
//        public void onAdClosed() {
//            super.onAdClosed();
//            Log.d(TAG, "onAdClosed..");
//        }
//
//        @Override
//        public void onAdFailedToLoad(int i) {
//            super.onAdFailedToLoad(i);
//            showBanner();
//            Log.d(TAG, "onAdFailedToLoad..");
//        }
//
//        @Override
//        public void onAdLeftApplication() {
//            super.onAdLeftApplication();
//            Log.d(TAG, "onAdLeftApplication..");
//        }
//
//        @Override
//        public void onAdOpened() {
//            super.onAdOpened();
//            Log.d(TAG, "onAdOpened..");
//        }
//
//        @Override
//        public void onAdLoaded() {
//            super.onAdLoaded();
//            showBanner();
//            Log.d(TAG, "onAdLoaded..");
//        }
//
//        @Override
//        public void onAdClicked() {
//            super.onAdClicked();
//            Log.d(TAG, "onAdClicked..");
//        }
//
//        @Override
//        public void onAdImpression() {
//            super.onAdImpression();
//            Log.d(TAG, "onAdImpression..");
//        }
//    };
}
