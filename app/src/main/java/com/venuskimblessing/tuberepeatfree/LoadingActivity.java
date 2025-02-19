package com.venuskimblessing.tuberepeatfree;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.venuskimblessing.tuberepeatfree.Common.CommonApiKey;
import com.venuskimblessing.tuberepeatfree.Common.CommonUserData;
import com.venuskimblessing.tuberepeatfree.FirebaseUtils.LogUtils;
import com.venuskimblessing.tuberepeatfree.Utils.SoftKeybordManager;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity implements RewardedVideoAdListener {
    public static final String TAG = "LoadingActivity";

    public static final String TYPE_KEY = "type";
    public static final String TYPE_FULL_AD = "full_ad";
    public static final String TYPE_REWARD_AD = "reward_ad";

    //보상형광고
    private RewardedVideoAd mRewardedVideoAd;

    //전면광고
    private InterstitialAd mInterstitialAd = null;

    private LinearLayout mLoadingLay;
    private AVLoadingIndicatorView mLoadingIndicator;

    //SoftKeyboard
    private SoftKeybordManager mSoftKeybordManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent = getIntent();
        String type = intent.getStringExtra(TYPE_KEY);

        mLoadingLay = (LinearLayout) findViewById(R.id.loading_lay);
        mLoadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_pacman_indicator);
        mLoadingIndicator.show();

        if (type.equals(TYPE_FULL_AD)) {
            loadFullAd();
        } else if (type.equals(TYPE_REWARD_AD)) {
            initRewardAd();
            loadRewardAd();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSoftKeybordManager = new SoftKeybordManager(getWindow());
            mSoftKeybordManager.hideSystemUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initRewardAd() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    private void loadRewardAd() {
        mRewardedVideoAd.loadAd(CommonApiKey.KEY_ADMOB_REWARD, new AdRequest.Builder().build());
    }

    /**
     * 전면 광고 노출
     */
    private void loadFullAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(CommonApiKey.KEY_ADMOB_INTRO_FULL_UNIT);
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
            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.VALUE, i);
            LogUtils.logEvent(LoadingActivity.this, "FullAd_onFailed", bundle);
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

    private void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }

    //보상형 광고 callback
    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded...");
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened...");

    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted...");

    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed...");
        finishActivity();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        LogUtils.logEvent(this, "reward_onRewarded", null);
        Toast.makeText(this, getString(R.string.unlocked_feature_reward_batterysaving_success), Toast.LENGTH_SHORT).show();
        CommonUserData.sRewardUnlockedFeatureBatterSaving = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication...");

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.VALUE, i);
        LogUtils.logEvent(this, "reward_onFailed", bundle);
        Log.d(TAG, "onRewardedVideoAdFailedToLoad..." + i);
        Toast.makeText(this, R.string.reward_allRemoveAd_loading, Toast.LENGTH_SHORT).show();
        finishActivity();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted...");

    }
}
