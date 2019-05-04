package com.venuskimblessing.youtuberepeatfree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.venuskimblessing.youtuberepeatfree.Common.CommonApiKey;
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
        loadFullAd();
        mLoadingLay = (LinearLayout) findViewById(R.id.loading_lay);
        mLoadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_pacman_indicator);
        mLoadingIndicator.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 전면 광고 노출
     */
    private void loadFullAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(CommonApiKey.KEY_ADMOB_TEST_UNIT_ID);
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
            finishActivity();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            Log.d(TAG, "onAdFailedToLoad");
            finishActivity();
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

    private void finishActivity(){
        setResult(RESULT_OK);
        finish();
    }
}
