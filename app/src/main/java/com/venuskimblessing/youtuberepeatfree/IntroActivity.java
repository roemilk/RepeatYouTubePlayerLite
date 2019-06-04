package com.venuskimblessing.youtuberepeatfree;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.common.Common;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.fade.FadeTextView;
import com.venuskimblessing.youtuberepeatfree.Billing.BillingManager;
import com.venuskimblessing.youtuberepeatfree.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonConfig;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatfree.FirebaseUtils.LogUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IntroActivity extends AppCompatActivity {
    public static final String TAG = "IntroActivity";

    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics;
    private final String EVENT_INVITATION_INSTALL = "InvitationUser";

    private FadeTextView mLineTextView = null;
    private String mPlayId = null;

    //RemoteConfig
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    //SoftKeyboard
    private SoftKeybordManager mSoftKeybordManager;

    //Inapp
    private BillingManager mBillingManager;

    private InterstitialAd mInterstitialAd = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mLineTextView = (FadeTextView) findViewById(R.id.intro_textView);
        initRemoteConfig();
        fetch();
        initQueryBilling();
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 앱 최초 실행인지 여부를 판단하여 알맞는 화면으로 이동합니다.
     */
    private void startActivity() {
        boolean guideSate = SharedPreferencesUtils.getBoolean(IntroActivity.this, CommonSharedPreferencesKey.KEY_GUIDE);
        Intent intent = null;
        if (guideSate) {
            intent = new Intent(IntroActivity.this, SearchActivity.class);
        } else {
            intent = new Intent(IntroActivity.this, GuideActivity.class);
        }
//        if(mPlayId != null){
//            intent.putExtra("videoId", mPlayId);
//        }
        startActivity(intent);
        finish();
    }

    /**
     * 구글 애널리틱스에 로그를 남깁니다.
     *
     * @param eventName
     */
    private void setEventLog(String eventName) {
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if (mFirebaseAnalytics != null) {
                mFirebaseAnalytics.logEvent(eventName, null);
            } else {
                Log.d(TAG, "FirebaseAnalytics is Null..");
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception : " + e.toString());
        }
    }

    /**
     * initRemoteConfig
     */
    private void initRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
    }

    /**
     * RemoteConfig fetch
     */
    private void fetch() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);
//                            Toast.makeText(IntroActivity.this, "Fetch and activate succeeded",
//                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Fetch failed...");
//                            Toast.makeText(IntroActivity.this, "Fetch failed",
//                                    Toast.LENGTH_SHORT).show();
                        }
                        setConfig();
                    }
                });
    }

    /**
     * RemoteConfig Variable setting
     */
    private void setConfig() {
        CommonConfig.sConfigRewardRemoveAllAdSate = mFirebaseRemoteConfig.getBoolean(CommonConfig.KEY_REWARD_REMOVEALLAD);
        Log.d(TAG, "setConfig : " + CommonConfig.sConfigRewardRemoveAllAdSate);
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "token : " + token);
                    }
                });
    }

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    private void startAnimationTextView() {
        boolean premiumState = SharedPreferencesUtils.getBoolean(this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION);
        if (premiumState) {
            mLineTextView.animateText(getResources().getString(R.string.app_name_premium));
        } else {
            mLineTextView.animateText(getResources().getString(R.string.app_name));
        }
        mLineTextView.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(HTextView hTextView) {
                if(CommonUserData.sPremiumState){
                    startActivity();
                }else{
                    loadFullAd();
                }
            }
        });
    }

    //인앱
    private void initQueryBilling() {
        mBillingManager = new BillingManager(this);
        mBillingManager.setOnQueryInventoryItemListener(new BillingManager.OnQueryInventoryItemListener() {
            @Override
            public void onPremiumVersionUser() {
//                Toast.makeText(IntroActivity.this, "프리미엄 사용자입니다.", Toast.LENGTH_SHORT).show();
                CommonUserData.sPremiumState = true;
                SharedPreferencesUtils.setBoolean(IntroActivity.this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, true);
                startAnimationTextView();
            }

            @Override
            public void onFreeVersionUser() {
//                Toast.makeText(IntroActivity.this, "프리 사용자입니다.", Toast.LENGTH_SHORT).show();
                CommonUserData.sPremiumState = false;
                SharedPreferencesUtils.setBoolean(IntroActivity.this, CommonSharedPreferencesKey.KEY_PREMIUM_VERSION, false);
                startAnimationTextView();
            }
        });
        mBillingManager.initBillingQueryInventoryItem();
    }

    /**
     * 전면 광고 노출
     */
    private void loadFullAd() {
        Log.d(TAG, "전면 광고 로드..");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(CommonApiKey.KEY_ADMOB_FULL_UNIT);
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showFullAd() {
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                startActivity();
            }
        } else {
            startActivity();
        }
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdClosed() {
            Log.d(TAG, "onAdClosed");
            super.onAdClosed();
            startActivity();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            LogUtils.logEvent(IntroActivity.this, "IntroActivity onAdFailedToLoad", null);
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
            showFullAd();
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
}
