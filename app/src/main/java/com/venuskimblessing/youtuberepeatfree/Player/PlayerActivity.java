/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.venuskimblessing.youtuberepeatfree.Player;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.venuskimblessing.youtuberepeatfree.BuyPremiumActivity;
import com.venuskimblessing.youtuberepeatfree.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatfree.Common.IntentAction;
import com.venuskimblessing.youtuberepeatfree.Common.IntentKey;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogBatterySaving;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogCommon;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogHelp;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogPickerCount;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogPickerTime;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogPlayList;
import com.venuskimblessing.youtuberepeatfree.Dialog.DialogPro;
import com.venuskimblessing.youtuberepeatfree.DynamicLink.DynamicLinkManager;
import com.venuskimblessing.youtuberepeatfree.FirebaseUtils.LogUtils;
import com.venuskimblessing.youtuberepeatfree.FloatingView.FloatingManager;
import com.venuskimblessing.youtuberepeatfree.FloatingView.FloatingService;
import com.venuskimblessing.youtuberepeatfree.Json.PlayingData;
import com.venuskimblessing.youtuberepeatfree.Json.Videos;
import com.venuskimblessing.youtuberepeatfree.LoadingActivity;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListDataManager;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitCommons;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitManager;
import com.venuskimblessing.youtuberepeatfree.Retrofit.RetrofitService;
import com.venuskimblessing.youtuberepeatfree.SearchActivity;
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;
import com.venuskimblessing.youtuberepeatfree.Utils.UIConvertUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 */
public class PlayerActivity extends YouTubeFailureRecoveryActivity implements View.OnClickListener, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener, RangeSeekBar.OnRangeSeekBarChangeListener<Integer>, ExpandableLayout.OnExpansionUpdateListener {
    public static final String TAG = "PlayerActivity";
    public static final String TAG_ACTIVITY = "activity_life";

    //Request Code
    public final int REQ_CODE_OVERLAY_PERMISSION = 123;
    public final int REQ_CODE_AD_FINISH_FLOATINGWINDOW = 1004;
    public final int REQ_CODE_REWARD_FINISH_BATTERYSAVING = 1005;

    //TYPE
    private final String TYPE_MIME = "text/plain";

    //YoutubePlayer
    private YouTubePlayerView mYouTubeView = null;
    private YouTubePlayer mYouTubePlayer = null;
    private YouTubePlayer.Provider mProvider = null;
    private boolean wasRestored = false;
    private RangeSeekBar<Integer> mRangeSeekBar = null;

    //Timer
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    //Top Menu
    private Button mHelpButton, mSearchButton, mBackButton, mFullscreenButton, mLockButton,
            mPlayListButton, mPopupButton, mShareButton, mPrevPlayButton, mNextPlayButton,
            mPlayButton, mReplayButton, mForwadButton, mBatterSavingButton;
    private TextView mTopCountTextView;

    //Bottom Menu
    private LinearLayout mSubFeatureLay, mBottomSettingLay;

    //ExpandableLayout
    private LinearLayout mExpandableContentLay_0 = null;
    private TextView mExpandableCountTextView;
    private EditText mStartTimeEditText, mEndTimeEditText, mRepeatCountEditText;
    private ExpandableLayout mExpandableLayout_0, mExpandableLayout_1;
    private TextView mExpandableButton_0;
    private Button mPlayListAddButton, mRepeatButton;

    private String mPlayId = null;
    private int mStartTime = 0; //시작시간
    private int mEndTime = 0; //끝시간
    private int mRepeatCount = 0; //반복횟수
    private boolean mLock = false;
    private boolean mRepeatInfinite = false; //무한반복

    //TimeDialog
    DialogPickerTime mDialogPickerTime = null;

    //Network
    private Retrofit mRetrofit = null;
    private RetrofitService mService = null;
    private Call<String> mCallVideos = null;
    private Gson mGson = new Gson();

    //PlayList
    public final int TYPE_NORMAL = 0; //일반 재생 영상
    public final int TYPE_PLAYLIST = 1; //플레이리스트 재생 영상

    private PlayListDataManager mPlayListDataManager = null;
    private Videos.Snippet mSnippet = null;
    private DialogPlayList mDialogPlayList = null;
    private ArrayList<PlayListData> mPlayListArray = null;
    private PlayListData mCurrentPlayListData = null;
    private int mPlayType = TYPE_NORMAL;
    private int mPlayIndex = 0;

    //flag
    private boolean mInvitationState = false;
    private boolean mFullScreenFlag = false; //풀스크린 상태

    //DynamicLink
    private DynamicLinkManager mDynamicLinkManager = null;

    //Facebook Share
    private CallbackManager mCallbackManager;
    private ShareDialog mShareDialog;

    //Shuffle
    private Button mShuffleButton;
    private Random mRandom = new Random();
    private boolean mShuffle = false;
    private int mShffleIndex = 0;

    //SoftKey
    private SoftKeybordManager mSoftKeybordManager;

    //BatterySaving
    private DialogBatterySaving mDialogBatterySaving;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_ACTIVITY, "onCreate...");
        setContentView(R.layout.activity_player);
//        checkPiracyChecker();
        getExtraData();
        initPlayList();
        initRetrofit();

        mDynamicLinkManager = new DynamicLinkManager(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, "PlayerActivity");
        LogUtils.logEvent(this, FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mHelpButton = (Button) findViewById(R.id.player_help_button);
        mHelpButton.setOnClickListener(this);

        mSearchButton = (Button) findViewById(R.id.player_search_button);
        mSearchButton.setOnClickListener(this);

        mBackButton = (Button) findViewById(R.id.player_back_button);
        mBackButton.setOnClickListener(this);

        mFullscreenButton = (Button) findViewById(R.id.player_fullscreen_button);
        mFullscreenButton.setOnClickListener(this);

        mLockButton = (Button) findViewById(R.id.player_top_lock_button);
        mLockButton.setOnClickListener(this);

        mTopCountTextView = (TextView) findViewById(R.id.player_top_count_textView);

        mSubFeatureLay = (LinearLayout) findViewById(R.id.player_setting_subFeature_lay);
        mBottomSettingLay = (LinearLayout) findViewById(R.id.player_setting_lay);
        mExpandableContentLay_0 = (LinearLayout) findViewById(R.id.expandable_content_lay_0);
        mExpandableLayout_0 = (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        mExpandableButton_0 = (TextView) findViewById(R.id.expand_button_0);
        mExpandableButton_0.setOnClickListener(this);

        mExpandableCountTextView = (TextView) findViewById(R.id.expand_count_textView);

        mStartTimeEditText = (EditText) findViewById(R.id.player_setting_startTime_editText);
        mEndTimeEditText = (EditText) findViewById(R.id.player_setting_endTime_editText);
//        mRepeatCountEditText = (EditText) findViewById(R.id.player_setting_repeatCount_editText);
        mStartTimeEditText.setOnClickListener(this);
        mEndTimeEditText.setOnClickListener(this);
//        mRepeatCountEditText.setOnClickListener(this);

        mYouTubeView = (YouTubePlayerView) findViewById(R.id.player_youtube_view);
        mYouTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);

        mPlayListButton = (Button) findViewById(R.id.player_top_playlist_button);
        mPlayListButton.setOnClickListener(this);

        mPlayListAddButton = (Button) findViewById(R.id.player_setting_playlist_button);
        mPlayListAddButton.setOnClickListener(this);

        mRepeatButton = (Button) findViewById(R.id.player_setting_repeat_button);
        mRepeatButton.setOnClickListener(this);

        mPopupButton = (Button) findViewById(R.id.player_top_popup_button);
        mPopupButton.setOnClickListener(this);

        mShareButton = (Button) findViewById(R.id.player_setting_share_button);
        mShareButton.setOnClickListener(this);

        //Feature
        mShuffleButton = (Button) findViewById(R.id.player_feature_shuffle_button);
        mShuffleButton.setOnClickListener(this);

        mPrevPlayButton = (Button) findViewById(R.id.player_feature_prev_button);
        mPrevPlayButton.setOnClickListener(this);

        mNextPlayButton = (Button) findViewById(R.id.player_feature_next_button);
        mNextPlayButton.setOnClickListener(this);

        mPlayButton = (Button) findViewById(R.id.player_feature_play_button);
        mPlayButton.setOnClickListener(this);

        mReplayButton = (Button) findViewById(R.id.player_feature_replay_5_button);
        mReplayButton.setOnClickListener(this);

        mForwadButton = (Button) findViewById(R.id.player_feature_forward_5_button);
        mForwadButton.setOnClickListener(this);

        mBatterSavingButton = (Button) findViewById(R.id.player_top_batterySaving_button);
        mBatterSavingButton.setOnClickListener(this);

        initRangeSeekBar();
        initPickerTime();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        mSoftKeybordManager = new SoftKeybordManager(getWindow()); //한번 소프트키보드가 보여지면 다시 안내려가는 이슈로 일단 보류...
//        mSoftKeybordManager.hideSystemUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart..");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_ACTIVITY, "onStart...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_ACTIVITY, "onResume...");
        getShareIntentData();
        startPlay(mPlayId);
        loadVideosDetails(mPlayId);
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
        Log.d(TAG, "onPause..");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG_ACTIVITY, "onCreate...");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "request code : " + requestCode + " result code : " + resultCode);
        if (requestCode == REQ_CODE_AD_FINISH_FLOATINGWINDOW) {
            if (resultCode == RESULT_OK) {
                showPopupFlaotingWindow();
                return;
            }
        } else if (requestCode == REQ_CODE_REWARD_FINISH_BATTERYSAVING) {
            if (resultCode == RESULT_OK) {
                if (CommonUserData.sRewardUnlockedFeatureBatterSaving) {
                    mDialogBatterySaving = new DialogBatterySaving(this, R.style.custom_dialog_fullScreen);
                    mDialogBatterySaving.show();
                    updateBatterSavingDialog();
                }
            }
        }
        if (mCallbackManager != null) { //Facebook callback manager
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess..");

        this.mYouTubePlayer = player;
        mYouTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                Log.d(TAG, "onFullscreen >> " + b);
                mFullScreenFlag = b;
            }
        });
        this.mProvider = provider;
        this.wasRestored = wasRestored;
        player.setPlaybackEventListener(this);
        player.setPlayerStateChangeListener(this);
        setStylePlayer();

        if (mPlayId != null) {
            startPlay(mPlayId);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.player_youtube_view);
    }

    /**
     * 유튜브로 부터 공유 메타데이터 수신
     */
    private void getShareIntentData() {
        Log.d(TAG, "유튜브 공유로부터 비디오 메타 데이터를 넘겨받습니다.");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (TYPE_MIME.equals(type)) {
                mPlayType = TYPE_NORMAL;
                String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "ShareText : " + shareText);

                mPlayId = shareText.substring(17);
                Log.d(TAG, "mPlayId : " + mPlayId);
            }
        } else if (IntentAction.INTENT_ACTION_SEARCH_PLAYLIST.equals(action)) {
            mPlayType = TYPE_PLAYLIST;
            PlayListData data = (PlayListData) intent.getSerializableExtra("data");
            startPlayListPlay(data);
        } else if (IntentAction.INTENT_ACTION_SHARE_VIDEO.equals(action)) {
            mPlayType = TYPE_NORMAL;
            String id = intent.getStringExtra(IntentKey.INTENT_KEY_ID);
            String startTime = intent.getStringExtra(IntentKey.INTENT_KEY_START);
            String endTime = intent.getStringExtra(IntentKey.INTENT_KEY_END);
            mPlayId = id;
            mStartTime = Integer.parseInt(startTime);
            mEndTime = Integer.parseInt(endTime);

            String convertStartTime = MediaUtils.getMillSecToHMS(mStartTime);
            String convertEndTime = MediaUtils.getMillSecToHMS(mEndTime);
            String prefixString = getString(R.string.range_share_prefix);
            Toast.makeText(this, prefixString + "\n\n" + convertStartTime + " - " + convertEndTime, Toast.LENGTH_LONG).show();

            if (!mExpandableLayout_0.isExpanded()) {
                mExpandableLayout_0.expand(true);
            }
        }
    }

    private void getExtraData() {
        Log.d(TAG, "리스트로부터 비디오 메타 데이터를 넘겨받습니다.");
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "getExtraData action >> " + action);

            if (action != null) {
                if (action.equals(IntentAction.INTENT_ACTION_SEARCH_PLAY)) {
                    mPlayType = TYPE_NORMAL;
                    String videoId = intent.getStringExtra("videoId");
                    if (videoId != null && !videoId.equals("")) {
                        Log.d(TAG, "getExtraData videoid : " + videoId);
                        mPlayId = videoId;
                    }
                }
            }
        } else {
            Log.d(TAG, "Intent가 널입니다.");
        }
    }

    private void initPlayList() {
        mPlayListDataManager = new PlayListDataManager(this);
        mPlayListDataManager.initTable();
    }

    private void initRangeSeekBar() {
        mRangeSeekBar = new RangeSeekBar<Integer>(this);
        mRangeSeekBar.setNotifyWhileDragging(true);
        mRangeSeekBar.setTextAboveThumbsColor(Color.parseColor("#00000000"));
        mRangeSeekBar.setOnRangeSeekBarChangeListener(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) UIConvertUtils.convertDpToPixel(-10, this);
        mRangeSeekBar.setLayoutParams(layoutParams);
        mExpandableContentLay_0.addView(mRangeSeekBar);
    }

    private void initPickerTime() {
        mDialogPickerTime = new DialogPickerTime(this, R.style.custom_dialog_fullScreen);
        mDialogPickerTime.setOnSelectedNumberPickerListener(new DialogPickerTime.OnSelectedNumberPickTimeListener() {
            @Override
            public void onSelectedTimeResult(int type, int duration) {
                String resultTimeString = MediaUtils.getMillSecToHMS(duration);
                switch (type) {
                    case DialogPickerTime.TYPE_START_TIME:
                        mStartTime = duration;
                        mYouTubePlayer.seekToMillis(mStartTime);
                        mRangeSeekBar.setSelectedMinValue(duration);
                        mStartTimeEditText.setText(resultTimeString);
                        break;
                    case DialogPickerTime.TYPE_END_TIME:
                        mEndTime = duration;
                        mRangeSeekBar.setSelectedMaxValue(duration);
                        mEndTimeEditText.setText(resultTimeString);
                        break;
                }
            }
        });
    }

    private void initData() {
        int duration = (int) mYouTubePlayer.getDurationMillis();

        if (mStartTime != 0) {
            mRangeSeekBar.setRangeValues(1000, duration);

            String startTime = MediaUtils.getMillSecToHMS(mStartTime);
            mStartTimeEditText.setText(startTime);
            mYouTubePlayer.seekToMillis(mStartTime);
            mRangeSeekBar.setSelectedMinValue(mStartTime);

            String endTime = MediaUtils.getMillSecToHMS(mEndTime);
            mEndTimeEditText.setText(endTime);
            mRangeSeekBar.setSelectedMaxValue(mEndTime);
        } else {
            mRangeSeekBar.setRangeValues(1000, duration);
            mStartTime = 1000;
            mEndTime = duration;
        }
        String startTime = MediaUtils.getMillSecToHMS((int) mStartTime);
        String endTime = MediaUtils.getMillSecToHMS((int) mEndTime);

        mStartTimeEditText.setText(startTime);
        mEndTimeEditText.setText(endTime);
    }

    private void setStylePlayer() {
        mYouTubePlayer.setShowFullscreenButton(true);
    }

    /**
     * 현재 화면 회전상태 알아오기
     *
     * @return
     */
    private int getScreenOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation;
    }

    /**
     * 잠금 상태에 따른 버튼의 이미지를 처리한다.
     */
    private void setLockButtonRes() {
        if (mLock) {
            mLockButton.setBackgroundResource(R.drawable.ic_lock_outline_white_24dp);
        } else {
            mLockButton.setBackgroundResource(R.drawable.ic_lock_open_white_24dp);
        }
    }

    /**
     * 잠금 상태에 따른 터치를 무효화한다.
     */
    private void setLock() {
        if (mLock) {
            mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            mBatterSavingButton.setVisibility(View.GONE);
            mSubFeatureLay.setVisibility(View.GONE);
            mBottomSettingLay.setVisibility(View.GONE);
            mHelpButton.setVisibility(View.GONE);
            mSearchButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mPlayListButton.setVisibility(View.GONE);
            mPopupButton.setVisibility(View.GONE);
            mFullscreenButton.setVisibility(View.INVISIBLE);
        } else {
            mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            mBatterSavingButton.setVisibility(View.VISIBLE);
            mSubFeatureLay.setVisibility(View.VISIBLE);
            mBottomSettingLay.setVisibility(View.VISIBLE);
            mHelpButton.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            mBackButton.setVisibility(View.VISIBLE);
            mPlayListButton.setVisibility(View.VISIBLE);
            mPopupButton.setVisibility(View.VISIBLE);
            mFullscreenButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 플레이리스트 재생 처리
     *
     * @param data
     */
    private void startPlayListPlay(PlayListData data) {
        Log.d(TAG, "startPlayListPlay...");
        getPlayListArray();
        mCurrentPlayListData = data;
        int id = data.getId();
        mPlayId = data.getVideoId();
        mPlayIndex = getPlayIndex(id);
        mStartTime = Integer.parseInt(data.getStartTime());
        mEndTime = Integer.parseInt(data.getEndTime());
        loadVideosDetails(mPlayId);
        if (SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY)) {
            startAutoPlay(mPlayId);
        } else {
            startPlay(mPlayId);
        }
    }

    /**
     * 일반 영상 재생
     *
     * @param id
     */
    private void startPlay(String id) {
        if (mYouTubePlayer != null) {
            try {
                mYouTubePlayer.loadVideo(id);
            } catch (IllegalStateException e) {
                Toast.makeText(this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                finish();
            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mYouTubePlayer.pause();
//                }
//            }, 1000);
        }
    }

    private void startAutoPlay(String videoId) {
        if (mYouTubePlayer != null) {
            stopTimer();
            mYouTubePlayer.loadVideo(videoId);

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mYouTubePlayer.play();
//                }
//            }, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expand_button_0:
                if (mExpandableLayout_0.isExpanded()) {
                    mExpandableLayout_0.collapse();
                } else {
                    mExpandableLayout_0.expand(true);
                }
                break;
//            case R.id.player_setting_repeatCount_editText:
//                DialogPickerCount dialogPickerCount = new DialogPickerCount(this, R.style.custom_dialog_fullScreen);
//                dialogPickerCount.setOnSelectedNumberPickerListener(onSelectedNumberPickerListener);
//                dialogPickerCount.show();
//                break;
            case R.id.player_setting_startTime_editText:
                mYouTubePlayer.pause();
                mDialogPickerTime.setType(DialogPickerTime.TYPE_START_TIME);
                mDialogPickerTime.setData(mYouTubePlayer.getDurationMillis(), mStartTime);
                mDialogPickerTime.show();
                break;
            case R.id.player_setting_endTime_editText:
                mYouTubePlayer.pause();
                mDialogPickerTime.setType(DialogPickerTime.TYPE_END_TIME);
                mDialogPickerTime.setData(mYouTubePlayer.getDurationMillis(), mEndTime);
                mDialogPickerTime.show();
                break;
            case R.id.player_help_button:
                LogUtils.logEvent(this, "feature_help", null);
                mYouTubePlayer.pause();
                DialogHelp dialogHelp = new DialogHelp(this, R.style.custom_dialog_fullScreen);
                dialogHelp.show();
                break;
            case R.id.player_search_button:
                LogUtils.logEvent(this, "feature_search", null);
                finish();
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.player_back_button:
                finish();
                break;
            case R.id.player_fullscreen_button:
                LogUtils.logEvent(this, "feature_fullscreen", null);
//                if (!mInvitationState) {
//                    showProDialog();
//                    return;
//                }

//                if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    mTopCountTextView.setVisibility(View.VISIBLE);
//                    hideMenu();
//                } else if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    mTopCountTextView.setVisibility(View.GONE);
//                    showMenu();
//                }

//                showUnLockSuccessDialog();

                if (mYouTubePlayer != null) {
                    mYouTubePlayer.setFullscreen(true);
                }
                break;

            case R.id.player_top_lock_button:
                LogUtils.logEvent(this, "feature_lock", null);
                if (checkUnlockShareFeature()) {
                    mLock = !mLock;
                    setLockButtonRes();
                    setLock();
                } else {
                    showShareLockedFeatureDialog(getString(R.string.locked_feature_lock_title));
                }
                break;

            case R.id.player_top_playlist_button:
                LogUtils.logEvent(this, "feature_playlist_show", null);
                mDialogPlayList = new DialogPlayList(this, R.style.custom_dialog_fullScreen);
                mDialogPlayList.setOnClickListener(new DialogPlayList.OnClickDialogPlayListListener() {
                    @Override
                    public void onPlay(PlayListData data) {
                        mDialogPlayList.dismiss();
                        mPlayType = TYPE_PLAYLIST;
                        startPlayListPlay(data);
                    }
                });
                mDialogPlayList.setOnClickControllerListener(new DialogPlayList.OnClickDialogControllerListener() {
                    @Override
                    public void play(View v) {
                        if (mYouTubePlayer.isPlaying()) {
                            mYouTubePlayer.pause();
                            v.setBackgroundResource(R.drawable.ic_play_arrow_gray_24dp);
                        } else {
                            mYouTubePlayer.play();
                            v.setBackgroundResource(R.drawable.ic_pause_gray_24dp);
                        }
                    }
                });
                mDialogPlayList.show();
                refreshPlayListController();
                break;

            case R.id.player_setting_playlist_button:
                LogUtils.logEvent(this, "feature_playlist_add", null);
                if (mSnippet != null) {
                    mPlayListArray = mPlayListDataManager.loadPlayList();

                    if (mPlayListArray != null) {
                        if (mPlayListArray.size() >= CommonUserData.PLAYLIST_LIMIT_COUNT) {
                            //제한 5회 다이얼로그 출력 프로버전 유도
                            showProDialog();
                            return;
                        }
                    }

                    PlayListData data = createPlayListData();
                    mPlayListArray.add(0, data);
//                    mPlayListDataManager.insert(img_url, title, String.valueOf(duration), mPlayId, String.valueOf(mStartTime), String.valueOf(mEndTime), String.valueOf(mRepeatCount));
                    mPlayListDataManager.insertAllList(mPlayListArray);
                    mPlayListButton.performClick();
                    Toast.makeText(this, getResources().getString(R.string.playlist_add), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.player_setting_repeat_button:
                LogUtils.logEvent(this, "feature_repeat", null);
                DialogPickerCount dialogPickerCount = new DialogPickerCount(this, R.style.custom_dialog_fullScreen);
                dialogPickerCount.setOnSelectedNumberPickerListener(onSelectedNumberPickerListener);
                dialogPickerCount.show();
                break;

            case R.id.player_top_popup_button:
//                mPlayListArray = mPlayListDataManager.loadPlayList();
//                PlayListData currentData = createPlayListData();
//                mPlayListArray.add(0, currentData);
//                startOverlayWindowService(this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !Settings.canDrawOverlays(this)) {
                    onObtainingPermissionOverlayWindow();
                } else {
                    if (CommonUserData.sPremiumState || CommonUserData.sRemoveAllAd) {
                        showPopupFlaotingWindow();
                    } else {
                        intent = new Intent(this, LoadingActivity.class);
                        intent.putExtra(LoadingActivity.TYPE_KEY, LoadingActivity.TYPE_FULL_AD);
                        startActivityForResult(intent, REQ_CODE_AD_FINISH_FLOATINGWINDOW);
                    }
                }
                break;

            case R.id.player_setting_share_button:
                LogUtils.logEvent(this, "feature_dynamic_link_send", null);
                String title = "empty";
                if (mSnippet != null) {
                    title = mSnippet.title;
                }
                mDynamicLinkManager.createShortDynamicLink(title, mPlayId, String.valueOf(mStartTime), String.valueOf(mEndTime));
                break;

            case R.id.player_feature_shuffle_button:
                LogUtils.logEvent(this, "feature_shuffle", null);
                if (checkUnlockShareFeature()) {
                    mShuffleButton.setSelected(!mShuffleButton.isSelected());
                    if (mShuffleButton.isSelected()) {
                        mPlayListArray = mPlayListDataManager.loadPlayList();
                        if (mPlayListArray != null) {
                            if (mPlayListArray.size() <= 1) {
                                Toast.makeText(PlayerActivity.this, getString(R.string.sub_feature_shuffle_empty), Toast.LENGTH_SHORT).show();
                                mShuffleButton.setSelected(false);
                                return;
                            } else {
                                SharedPreferencesUtils.setBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY, true);
                                mShuffle = true;
                                startPlayListPlay(mPlayListArray.get(0));
                                Toast.makeText(PlayerActivity.this, getString(R.string.sub_feature_shuffle_start), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        mShuffle = false;
                        Toast.makeText(PlayerActivity.this, getString(R.string.sub_feature_shuffle_cancel), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showShareLockedFeatureDialog(getString(R.string.locked_feature_shuffle_title));
                }
                break;

            case R.id.player_feature_prev_button:
                prevSkipPlay();
                break;
            case R.id.player_feature_next_button:
                nextSkipPlay();
                break;
            case R.id.player_feature_play_button:
                if (mYouTubePlayer.isPlaying()) {
                    mYouTubePlayer.pause();
                    mPlayButton.setBackgroundResource(R.drawable.ic_play_arrow_24dp);
                } else {
                    mYouTubePlayer.play();
                    mPlayButton.setBackgroundResource(R.drawable.ic_pause_24dp);
                }
                break;
            case R.id.player_feature_replay_5_button:
                LogUtils.logEvent(this, "feature_replay_5sec", null);
                long replayCurrentMills = mYouTubePlayer.getCurrentTimeMillis();
                replayCurrentMills = replayCurrentMills - 5000;
                if (replayCurrentMills <= 0) {
                    mYouTubePlayer.seekToMillis(0);
                } else {
                    mYouTubePlayer.seekToMillis((int) replayCurrentMills);
                }
                break;
            case R.id.player_feature_forward_5_button:
                LogUtils.logEvent(this, "feature_forward_5sec", null);
                long currentForwardMills = mYouTubePlayer.getCurrentTimeMillis();
                long durationMills = mYouTubePlayer.getDurationMillis();
                durationMills = durationMills - 1000;
                currentForwardMills = currentForwardMills + 5000;
                if (currentForwardMills >= durationMills) {
                    return;
                } else {
                    mYouTubePlayer.seekToMillis((int) currentForwardMills);
                }
                break;
            case R.id.player_top_batterySaving_button:
                LogUtils.logEvent(this, "feature_battery_mode", null);
                int musicModeLimitCount = SharedPreferencesUtils.getInt(PlayerActivity.this, CommonSharedPreferencesKey.KEY_FEATURE_REWARD_UNLOCK_MUSIC_MODE_COUNT);
                boolean overLimitCount = true;
                if(musicModeLimitCount >= CommonUserData.MUSIC_MODE_LIMITED_COUNT){ //체험 제한 횟수 초과 여부 체크
                    overLimitCount = false;
                }

                if (CommonUserData.sPremiumState == true || CommonUserData.sRewardUnlockedFeatureBatterSaving == true || overLimitCount == true) {
                    if(overLimitCount){
                        musicModeLimitCount++;
                        SharedPreferencesUtils.setInt(PlayerActivity.this, CommonSharedPreferencesKey.KEY_FEATURE_REWARD_UNLOCK_MUSIC_MODE_COUNT, musicModeLimitCount);
                    }
                    mDialogBatterySaving = new DialogBatterySaving(this, R.style.custom_dialog_fullScreen);
                    mDialogBatterySaving.show();
                    updateBatterSavingDialog();
                } else {
                    showRewardLockedFeatureDialog();
                }
                break;
        }
    }

    private DialogPickerCount.OnSelectedNumberPickerListener onSelectedNumberPickerListener = new DialogPickerCount.OnSelectedNumberPickerListener() {
        @Override
        public void onSelectedValue(int value) {
            mRepeatCount = value;
            if (value <= 0) {
                mRepeatCount = 0;
                mRepeatInfinite = true;
                String infiniteString = getResources().getString(R.string.dialog_numberpick_infinite);
                mExpandableCountTextView.setText(infiniteString);
            } else {
                mRepeatCount = value;
                mRepeatInfinite = false;
                mExpandableCountTextView.setText(String.valueOf(mRepeatCount));
            }
            mExpandableCountTextView.setVisibility(View.VISIBLE);
            mTopCountTextView.setText(String.valueOf(mRepeatCount));
        }
    };

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
        Log.d(TAG, "startTime : " + mStartTime + " endTime : " + mEndTime);
        String startTime = null;
        if (mStartTime != minValue) {
            mStartTime = minValue;
            startTime = MediaUtils.getMillSecToHMS((int) minValue);
            mStartTimeEditText.setText(startTime);

            if (mYouTubePlayer != null) {
                mYouTubePlayer.seekToMillis(mStartTime);
            } else {
                Toast.makeText(PlayerActivity.this, "sorry.. network error please restart player", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mEndTime = maxValue;
        String endTime = MediaUtils.getMillSecToHMS((int) maxValue);
        mEndTimeEditText.setText(endTime);
    }

    private void startTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = mYouTubePlayer.getCurrentTimeMillis();
                repeatPlayRange(currentTime);
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 2000, 10);
    }

    private void stopTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    /**
     * 현재 재생 영상의 정보 저장
     */
    private void savePlayingData() {
        PlayingData data = new PlayingData();
        data.setVideoId(mPlayId);
        data.setStartTime(mStartTime);
        data.setEndTime(mEndTime);
        data.setRepeatCount(mRepeatCount);

        String json = mGson.toJson(data);
        Log.d(TAG, "PlayingData Json : " + json);
    }

    /**
     * 구간영상 반복재생 (range repeat play)
     *
     * @param currentTime
     */
    private void repeatPlayRange(long currentTime) {
//        Log.d(TAG, "currentTime : " + currentTime + " endTime : " + mEndTime);

        if (mRepeatInfinite) { //무한 반복
            if (currentTime >= mEndTime - 500) {
                mYouTubePlayer.seekToMillis(mStartTime);
                mYouTubePlayer.play();
            }
            return;
        }

        if (mRepeatCount - 1 > 0) {
            if (currentTime >= mEndTime - 500) { //반복 횟수가 설정된 경우
                mYouTubePlayer.seekToMillis(mStartTime);
                mYouTubePlayer.play();
                mRepeatCount--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExpandableCountTextView.setText(String.valueOf(mRepeatCount));
                        mTopCountTextView.setText(String.valueOf(mRepeatCount));
                    }
                });
            }
        } else {
            if (currentTime >= mEndTime) { //반복 횟수가 설정되지 않은 경우
                if (mRepeatCount > 0) { //영상을 유튜브플레이버튼을 눌러서 시작하는 경우는 타이머의 조건에 포함되지 않아 최초플레이의 경우 RepeatCount 한개가 감소되지 않고 시작하는 문제가 있어 RepeatCount를 표시하는 텍스트뷰와 싱크를 맞추기 위해 마지막 부분에서 감소처리하여 싱크를 맞춘다.
                    mRepeatCount--;
                }

                mYouTubePlayer.pause();
                mYouTubePlayer.seekToMillis(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExpandableCountTextView.setText(String.valueOf(mRepeatCount));
                        mTopCountTextView.setText(String.valueOf(mRepeatCount));
                        if (mPlayType == TYPE_PLAYLIST) {
                            boolean autuplayState = SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY);
                            if (autuplayState) {
                                Log.d(TAG, "자동 플레이가 설정되어 있습니다.");
                                if (isNext()) {
                                    Log.d(TAG, "다음 영상이 존재합니다.");
                                    nextPlay();
                                    updateBatterSavingDialog();
                                } else {
                                    Log.d(TAG, "다음 영상이 존재하지 않습니다. 다시 처음부터 재생합니다.");
                                    if (mPlayListArray.size() != 0) {
                                        mPlayIndex = 0;
                                        PlayListData data = mPlayListArray.get(mPlayIndex);
                                        startPlayListPlay(data);
                                        updateBatterSavingDialog();
                                    }
                                }
                            } else {
                                Log.d(TAG, "자동 플레이가 지정되어 있지 않습니다.");
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * State Callback Listener
     */
    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying...");
        mPlayButton.setBackgroundResource(R.drawable.ic_pause_24dp);
        if (mStartTime == mEndTime || mStartTime >= mEndTime) {
            mYouTubePlayer.pause();
            return;
        }
        stopTimer();
        startTimer();
    }

    @Override
    public void finish() {
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 회전상태 복원
        super.finish();
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused...");
        mPlayButton.setBackgroundResource(R.drawable.ic_play_arrow_24dp);
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
        stopService(new Intent(this, FloatingService.class));
        super.onDestroy();
        Log.d(TAG, "onDestroy..");
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped...");
    }

    @Override
    public void onBackPressed() {
        if (mLock) { //잠금 상태면 무시
            return;
        }

        if (mFullScreenFlag) { //풀스크린일때는 풀스크린 해제
            mYouTubePlayer.setFullscreen(false);
        } else {
            finish();
        }
    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onLoading() {
        Log.d(TAG, "onLoading");
    }

    @Override
    public void onLoaded(String s) {
        Log.d(TAG, "onLoaded");
        initData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mYouTubePlayer != null) {
                    try {
                        mYouTubePlayer.play();
                    } catch (IllegalStateException e) {
                        finish();
                    }
                }
            }
        }, 500);
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        Log.d(TAG, "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
        Log.d(TAG, "onVideoEnded");
//        if (mRepeatCount == 0) {
//            repeatPlayFull();
//        }
        stopTimer();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onExpansionUpdate(float expansionFraction, int state) {
        switch (state) {
            case ExpandableLayout.State.COLLAPSED:

                break;
            case ExpandableLayout.State.EXPANDED:

                break;
        }
    }

    private void initRetrofit() {
        mService = RetrofitManager.getRetrofitService(RetrofitCommons.BASE_URL);
    }

    /**
     * 비디오의 상세 정보를 가져온다.
     */
    private void loadVideosDetails(String id) {
        mCallVideos = mService.getYoutubeVideos("snippet,contentDetails,statistics", id, CommonApiKey.KEY_API_YOUTUBE);
        mCallVideos.enqueue(callback);
    }

    //getPlayListArray
    private void getPlayListArray() {
        mPlayListArray = mPlayListDataManager.loadPlayList();
    }

    private boolean isNext() {
        mPlayListArray = mPlayListDataManager.loadPlayList();

        if (mShuffle) { //무작위 재생
//            mPlayIndex = shufflePlayIndex();
            Log.d(TAG, "Shuffle 재생입니다.");
            shufflePlayIndex();
            mPlayIndex = mShffleIndex;
            return true;
        } else {
            int id = mCurrentPlayListData.getId();
            mPlayIndex = getPlayIndex(id);
        }

        if (mPlayType == TYPE_NORMAL) {
            if (mPlayListArray.size() != 0) {
                return true;
            } else {
                return false;
            }
        } else if (mPlayType == TYPE_PLAYLIST) {
            if (mPlayListArray.size() > mPlayIndex + 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 다음 영상을 재생합니다.
     */
    private void nextPlay() {
        Log.d(TAG, "다음 영상을 재생합니다.");
        if (mPlayType == TYPE_PLAYLIST) {
            if (!mShuffle) {
                mPlayIndex++;
            }

            mCurrentPlayListData = mPlayListArray.get(mPlayIndex);
            mPlayId = mCurrentPlayListData.getVideoId();
            mStartTime = Integer.parseInt(mCurrentPlayListData.getStartTime());
            mEndTime = Integer.parseInt(mCurrentPlayListData.getEndTime());
            //            mRepeatCount = Integer.parseInt(mCurrentPlayListData.getRepeat());
            startAutoPlay(mPlayId);
            refreshPlayListController();
        }
    }


    /**
     * 인덱스 셔플
     */
    private void shufflePlayIndex() {
        int allIndex = mPlayListArray.size();
//        Log.d(TAG, "shuffle allIndex >> " + allIndex);

        int shuffleIndex = mRandom.nextInt(allIndex);
        if (shuffleIndex == mShffleIndex) {
            Log.d(TAG, "shuffleIndex and prev shuffle is euqal.. re try random. " + shuffleIndex + " " + mShffleIndex);
            shufflePlayIndex();
            return;
        } else {
            Log.d(TAG, "shuffleIndex and prev shuffle is not euqal.. success " + shuffleIndex);
            mShffleIndex = shuffleIndex;
            return;
        }
    }

    /**
     * 이전 영상을 재생합니다.
     */
    private void prevSkipPlay() {
        if (mPlayType == TYPE_PLAYLIST) {
            if (mPlayIndex <= 0) {
                Toast.makeText(PlayerActivity.this, getString(R.string.prev_play_empty), Toast.LENGTH_SHORT).show();
                return;
            } else {
                mPlayIndex--;
                mCurrentPlayListData = mPlayListArray.get(mPlayIndex);
                mPlayId = mCurrentPlayListData.getVideoId();
                mStartTime = Integer.parseInt(mCurrentPlayListData.getStartTime());
                mEndTime = Integer.parseInt(mCurrentPlayListData.getEndTime());
                startAutoPlay(mPlayId);
                refreshPlayListController();
            }
        } else {
            Toast.makeText(PlayerActivity.this, getString(R.string.required_playlist_feature), Toast.LENGTH_SHORT).show();
        }
    }

    private void nextSkipPlay() {
        if (mPlayType == TYPE_PLAYLIST) {
            if (isNext()) {
                nextPlay();
            } else {
                Toast.makeText(PlayerActivity.this, getString(R.string.next_play_empty), Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(PlayerActivity.this, getString(R.string.required_playlist_feature), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 현재 재생되고 있는 영상의 인덱스 위치를 탐색한다.
     */
    private int getPlayIndex(int id) {
        int index = -1;

        for (int i = 0; i < mPlayListArray.size(); i++) {
            if (mPlayListArray.get(i).getId() == id) {
                index = i;
                break;
            }
        }
        Log.d(TAG, "getPlayIndex Index Search Detected : " + index + " title : " + mPlayListArray.get(index).getTitle());
        return index;
    }


    //PlayList Controller

    /**
     * PlayList의 컨트롤러를 업데이트 한다.
     */
    private void refreshPlayListController() {
        if (mDialogPlayList != null) {
            if (mDialogPlayList.isShowing()) {
                if (mPlayType == TYPE_NORMAL) {
                    if (mSnippet != null) {
                        String thumbUrl = mSnippet.getThumbnails().getMedium().getUrl();
                        String title = mSnippet.title;
                        mDialogPlayList.refreshController(thumbUrl, title, mYouTubePlayer.isPlaying());
                    }
                } else if (mPlayType == TYPE_PLAYLIST) {
                    if (mCurrentPlayListData != null) {
                        String thumbUrl = mCurrentPlayListData.getImg_url();
                        String title = mCurrentPlayListData.getTitle();
                        mDialogPlayList.refreshController(thumbUrl, title, mYouTubePlayer.isPlaying());
                    }
                }
            }
        }
    }

    //통신처리
    private Callback<String> callback = new Callback<String>() {
        @Override
        public void onResponse(Call<String> call, Response<String> response) {
            String result = null;
            try {
                result = response.body().toString();
            } catch (NullPointerException e) {
                return;
            }

            Log.d(TAG, "onResponse...");
            if (call == mCallVideos) {
                Videos videos = (Videos) mGson.fromJson(result, Videos.class);

                Videos.PageInfo pageInfo = videos.pageInfo;
                String totalResults = pageInfo.totalResults;
                if (totalResults.equals("0")) {
                    Toast.makeText(PlayerActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_SHORT);
                } else {
                    if (videos != null) {
                        ArrayList<Videos.Item> items = videos.items;
                        if (items != null) {
                            if (items.size() > 0) {
                                mSnippet = videos.items.get(0).snippet;
                            } else {
                                Toast.makeText(PlayerActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_SHORT);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
            Log.d(TAG, "t " + t.toString());
        }
    };

    //Dialog
    public void showProDialog() {
        if (mYouTubePlayer != null) {
            if (mYouTubePlayer.isPlaying()) {
                mYouTubePlayer.pause();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            finish();
        }

        final DialogPro dialogPro = new DialogPro(this, R.style.custom_dialog_fullScreen);
        dialogPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (v.getId()) {
                    case R.id.dialog_pro_button:
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.venuskimblessing.youtuberepeat"));
                        startActivity(intent);
                        break;

                    case R.id.dialog_invite_button:
                        setResult(RESULT_OK);
                        finish();
                        break;

                    case R.id.dialog_cancel_button:
                        dialogPro.dismiss();
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialogPro.show();
    }

    public void onObtainingPermissionOverlayWindow() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    private PlayListData createPlayListData() {
        String img_url = null;
        try {
            img_url = mSnippet.getThumbnails().getMedium().getUrl();
        } catch (NullPointerException e) {
            img_url = "";
        }
        String title = mSnippet.title;
        long duration = mYouTubePlayer.getDurationMillis();

        PlayListData firstPlayListData = new PlayListData();
        firstPlayListData.setImg_url(img_url);
        firstPlayListData.setTitle(title);
        firstPlayListData.setDuration(String.valueOf(duration));
        firstPlayListData.setVideoId(mPlayId);
        firstPlayListData.setStartTime(String.valueOf(mStartTime));
        firstPlayListData.setEndTime(String.valueOf(mEndTime));
        firstPlayListData.setRepeat(String.valueOf(mRepeatCount));
        return firstPlayListData;
    }

    public void goHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
        intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
        startActivity(intent);
    }

    private void showPopupFlaotingWindow() {
        LogUtils.logEvent(this, "feature_floating", null);
//        PlayListData currentData = createPlayListData();
//        mPlayListArray.add(0, currentData);
        Intent intent = new Intent(this, FloatingService.class);
        if(mPlayType == TYPE_PLAYLIST){
            mPlayListArray = mPlayListDataManager.loadPlayList();
            boolean autoPlay = SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY);
            if (autoPlay && mPlayListArray.size() > 0) {
                Log.d(TAG, "autoplay...");
                intent.putExtra("type", FloatingManager.TYPE_INTENT_LIST);
                intent.putExtra("list", mPlayListArray);
                intent.putExtra("index", mPlayIndex);
            } else {
                Log.d(TAG, "not autoplay...");
                intent.putExtra("type", FloatingManager.TYPE_INTENT_DATA);
                PlayListData data = createPlayListData();
                intent.putExtra("data", data);
                intent.putExtra("repeatcount", mRepeatCount);
                intent.putExtra("infinite", mRepeatInfinite);
            }
        }else{
            Log.d(TAG, "not autoplay...");
            intent.putExtra("type", FloatingManager.TYPE_INTENT_DATA);
            PlayListData data = createPlayListData();
            intent.putExtra("data", data);
            intent.putExtra("repeatcount", mRepeatCount);
            intent.putExtra("infinite", mRepeatInfinite);
        }
        startService(intent);
        goHome();
    }

    //FaceBook 공유
    private void shareFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(this);
        mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("facebookcallback", "facebookcallback onSuccess..");
                LogUtils.logEvent(PlayerActivity.this, "feature_facebook_share_success", null);
                SharedPreferencesUtils.setBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_FEATURE_SHARE_UNLOCK, true);
                showUnLockSuccessDialog();
            }

            @Override
            public void onCancel() {
                Log.d("facebookcallback", "facebookcallback onCancel..");
                Toast.makeText(PlayerActivity.this, getString(R.string.locked_feature_share_err), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebookcallback", "facebookcallback onError.." + error.toString());

            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareHashtag shareHashtag = new ShareHashtag.Builder().setHashtag("#youtube").build();
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.venuskimblessing.youtuberepeatfree"))
                    .setShareHashtag(shareHashtag)
                    .build();
            mShareDialog.show(linkContent);
        }
    }

    /**
     * Locked Feature Dialog
     * 잠금이 걸려 있으나 페이스북 공유로 언락할 수 있는 기능(잠금기능)
     */
    private void showShareLockedFeatureDialog(String title) {
        final DialogCommon dialogCommonLockedFeature = new DialogCommon(this, R.style.custom_dialog_fullScreen);
        dialogCommonLockedFeature.setTitle(title);
        dialogCommonLockedFeature.setContent(getString(R.string.locked_feature_share_content));
        dialogCommonLockedFeature.mThreeButton.setVisibility(View.GONE);
        dialogCommonLockedFeature.mOneButton.setText(getString(R.string.locked_feature_buyPro));
        dialogCommonLockedFeature.mTwoButton.setText(getString(R.string.locked_feature_share));
        dialogCommonLockedFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_common_one_button:
                        Intent intent = new Intent(PlayerActivity.this, BuyPremiumActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.dialog_common_two_button:
                        shareFacebook();
                        break;
                }
                dialogCommonLockedFeature.dismiss();
            }
        });
        dialogCommonLockedFeature.show();
    }

    /**
     * Locked Feature Dialog
     * 잠금이 걸려 있으나 리워드 감상으로 언락할 수 있는 기능(뮤직 모드)
     */
    private void showRewardLockedFeatureDialog() {
        final DialogCommon dialogCommonLockedFeature = new DialogCommon(this, R.style.custom_dialog_fullScreen);
        dialogCommonLockedFeature.setTitle(getString(R.string.locked_featrue_battery_title));
        dialogCommonLockedFeature.setContent(getString(R.string.locked_feature_reward_content));
        dialogCommonLockedFeature.mThreeButton.setVisibility(View.GONE);
        dialogCommonLockedFeature.mOneButton.setText(getString(R.string.locked_feature_buyPro));
        dialogCommonLockedFeature.mTwoButton.setText(getString(R.string.locked_feature_reward));
        dialogCommonLockedFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.dialog_common_one_button:
                        intent = new Intent(PlayerActivity.this, BuyPremiumActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.dialog_common_two_button:
                        //리워드 광고
                        LogUtils.logEvent(PlayerActivity.this, "feature_reward_button", null);
                        intent = new Intent(PlayerActivity.this, LoadingActivity.class);
                        intent.putExtra(LoadingActivity.TYPE_KEY, LoadingActivity.TYPE_REWARD_AD);
                        startActivityForResult(intent, REQ_CODE_REWARD_FINISH_BATTERYSAVING);
                        break;
                }
                dialogCommonLockedFeature.dismiss();
            }
        });
        dialogCommonLockedFeature.show();
    }

    private void showUnLockSuccessDialog() {
        DialogCommon dialogCommonUnlockSuccess = new DialogCommon(this, R.style.custom_dialog_fullScreen);
        dialogCommonUnlockSuccess.setTitle(getString(R.string.unlocked_feature_title));
        dialogCommonUnlockSuccess.setContent(getString(R.string.unlocked_feature_content));
        dialogCommonUnlockSuccess.hideButtonLay();
        dialogCommonUnlockSuccess.show();
    }

    /**
     * 베터리세이빙 다이얼로그 업데이트
     */
    private void updateBatterSavingDialog() {
        if (mDialogBatterySaving != null) {
            if (mDialogBatterySaving.isShowing()) {
                String currentTitle = "";
                if (mPlayType == TYPE_NORMAL) {
                    currentTitle = mSnippet.title;
                } else if (mPlayType == TYPE_PLAYLIST) {
                    currentTitle = mCurrentPlayListData.getTitle();
                }
                String convertStartTime = MediaUtils.getMillSecToHMS(mStartTime);
                String convertEndTime = MediaUtils.getMillSecToHMS(mEndTime);
                String time = convertStartTime + " - " + convertEndTime;
                mDialogBatterySaving.setTitle(currentTitle);
                mDialogBatterySaving.setTime(time);
            }
        }
    }

    /**
     * 잠금 해제된 기능인지 체크한다.(공유로 잠금해제 가능한 기능)
     *
     * @return
     */
    private boolean checkUnlockShareFeature() {
        boolean shareUnlock = SharedPreferencesUtils.getBoolean(this, CommonSharedPreferencesKey.KEY_FEATURE_SHARE_UNLOCK);

        if (CommonUserData.sPremiumState == true || shareUnlock == true) {
            return true;
        } else {
            return false;
        }
    }
}
