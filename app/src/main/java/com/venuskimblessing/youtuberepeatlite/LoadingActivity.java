package com.venuskimblessing.youtuberepeatlite;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.dd.morphingbutton.MorphingButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.internal.service.Common;
import com.venuskimblessing.youtuberepeatlite.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatlite.Player.PlayerActivity;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity {
    public static final String TAG = "LoadingActivity";

    //전면광고
    private InterstitialAd mInterstitialAd = null;

    private LinearLayout mLoadingLay;
    private AVLoadingIndicatorView mLoadingIndicator;

    private String mPlayId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getExtraData();

        int isCheckAdShow = CommonUserData.sAdCount %  CommonUserData.AD_DEALY_COUNT;
        Log.d(TAG, "isCheckAdShow : " + isCheckAdShow);

        if(isCheckAdShow == 0){
            MobileAds.initialize(this, CommonApiKey.KEY_ADMOB_APP_ID);
            showFullAd();

            mLoadingLay = (LinearLayout)findViewById(R.id.loading_lay);
            mLoadingIndicator = (AVLoadingIndicatorView)findViewById(R.id.loading_pacman_indicator);
            mLoadingIndicator.show();
        }else{
            startActivity();
        }

        CommonUserData.sAdCount++;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getExtraData(){
        Intent intent = getIntent();
        if(intent != null){
            String videoId = intent.getStringExtra("videoId");
            if(videoId != null && !videoId.equals("")){
                mPlayId = videoId;
            }
        }else{
            Log.d(TAG, "Intent가 널입니다.");
        }
    }

    /**
     * 전면 광고 노출
     */
    private void showFullAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(CommonApiKey.KEY_ADMOB_FULL_UNIT);
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private AdListener adListener = new AdListener(){
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
            startActivity();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            Log.d(TAG, "onAdFailedToLoad");
            startActivity();
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onLoaded...");

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
        }
    };

    private void startActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, PlayerActivity.class);

                if(mPlayId != null){
                    intent.putExtra("videoId", mPlayId);
                }

                startActivityForResult(intent, SearchActivity.REQUEST_PLAYER_INVITE);
            }
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult" + requestCode);
        if(requestCode == SearchActivity.REQUEST_PLAYER_INVITE){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }else{
                finish();
            }
        }
    }
}
