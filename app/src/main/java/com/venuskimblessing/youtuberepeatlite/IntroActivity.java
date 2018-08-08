package com.venuskimblessing.youtuberepeatlite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hanks.htextview.fade.FadeTextView;
import com.hanks.htextview.line.LineTextView;
import com.venuskimblessing.youtuberepeatlite.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatlite.Json.SearchList;
import com.venuskimblessing.youtuberepeatlite.Player.DeveloperKey;
import com.venuskimblessing.youtuberepeatlite.Player.PlayerActivity;
import com.venuskimblessing.youtuberepeatlite.Player.YouTubeFailureRecoveryActivity;
import com.venuskimblessing.youtuberepeatlite.Utils.SharedPreferencesUtils;

public class IntroActivity extends AppCompatActivity {
    public static final String TAG = "IntroActivity";

    private FadeTextView mLineTextView = null;
    private String mPlayId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
//        getShareIntentData();

        mLineTextView = (FadeTextView) findViewById(R.id.intro_textView);
        mLineTextView.animateText(getResources().getString(R.string.app_name));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity();
                finish();
            }
        }, 2000);
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
    }
}
