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

package com.venuskimblessing.youtuberepeatlite.Player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.venuskimblessing.youtuberepeatlite.Common.CommonApiKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogHelp;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogPickerCount;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogPickerTime;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogPlayList;
import com.venuskimblessing.youtuberepeatlite.Dialog.DialogPro;
import com.venuskimblessing.youtuberepeatlite.FloatingView.FloatingManager;
import com.venuskimblessing.youtuberepeatlite.FloatingView.FloatingService;
import com.venuskimblessing.youtuberepeatlite.Json.PlayingData;
import com.venuskimblessing.youtuberepeatlite.Json.Videos;
import com.venuskimblessing.youtuberepeatlite.LoadingActivity;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListDataManager;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitCommons;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitManager;
import com.venuskimblessing.youtuberepeatlite.Retrofit.RetrofitService;
import com.venuskimblessing.youtuberepeatlite.SearchActivity;
import com.venuskimblessing.youtuberepeatlite.Utils.MediaUtils;
import com.venuskimblessing.youtuberepeatlite.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatlite.Utils.UIConvertUtils;
import net.cachapa.expandablelayout.ExpandableLayout;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import java.util.ArrayList;
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

    //Request Code
    public final int REQ_CODE_OVERLAY_PERMISSION = 123;
    public final int REQ_CODE_AD_FINISH = 1004;

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
    private Button mHelpButton, mSearchButton, mBackButton, mOrientationButton, mLockButton, mPlayListButton, mPopupButton;
    private TextView mTopCountTextView;

    //Bottom Menu
    private LinearLayout mBottomSettingLay;

    //ExpandableLayout
    private LinearLayout mExpandableContentLay_0 = null;
    private TextView mExpandableCountTextView;
    private EditText mStartTimeEditText, mEndTimeEditText, mRepeatCountEditText;
    private ExpandableLayout mExpandableLayout_0, mExpandableLayout_1;
    private TextView mExpandableButton_0, mExpandableButton_1;
    private Button mPlayListAddButton, mRepeatButton;

    private String mPlayId = null;
    private int mStartTime = 0; //시작시간
    private int mEndTime = 0; //끝시간
    private int mRepeatCount = 0; //반복횟수
    private boolean mLock = false;

    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics;

    //TimeDialog
    DialogPickerTime mDialogPickerTime = null;

    //화면전환
    private OrientationEventListener mOrientationEventListener = null;

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

    //Invitation
    private boolean mInvitationState = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
//        checkPiracyChecker();
        initInviteItem();
        getExtraData();
        initPlayList();
        initRetrofit();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT, "플레이 화면");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mHelpButton = (Button) findViewById(R.id.player_help_button);
        mHelpButton.setOnClickListener(this);

        mSearchButton = (Button) findViewById(R.id.player_search_button);
        mSearchButton.setOnClickListener(this);

        mBackButton = (Button) findViewById(R.id.player_back_button);
        mBackButton.setOnClickListener(this);

        mOrientationButton = (Button) findViewById(R.id.player_screen_orientation_button);
        mOrientationButton.setOnClickListener(this);

        mLockButton = (Button) findViewById(R.id.player_top_lock_button);
        mLockButton.setOnClickListener(this);

        mTopCountTextView = (TextView) findViewById(R.id.player_top_count_textView);

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

        initRangeSeekBar();
        initPickerTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShareIntentData();
        startPlay(mPlayId);
        loadVideos(mPlayId);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause..");
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "request code : " + requestCode + " result code : " + resultCode);
        if(requestCode == REQ_CODE_AD_FINISH){
            if(resultCode == RESULT_OK){
                showPopupFlaotingWindow();
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess..");

        this.mYouTubePlayer = player;
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
                String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(TAG, "ShareText : " + shareText);

                mPlayId = shareText.substring(17);
                Log.d(TAG, "mPlayId : " + mPlayId);
            }
        }
    }

    private void getExtraData() {
        Log.d(TAG, "리스트로부터 비디오 메타 데이터를 넘겨받습니다.");

        Intent intent = getIntent();
        if (intent != null) {
            String videoId = intent.getStringExtra("videoId");
            if (videoId != null && !videoId.equals("")) {
                mPlayId = videoId;
            }
        } else {
            Log.d(TAG, "Intent가 널입니다.");
        }
    }

    private void initPlayList(){
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

    private void initPickerTime(){
        mDialogPickerTime = new DialogPickerTime(this, R.style.custom_dialog_fullScreen);
        mDialogPickerTime.setOnSelectedNumberPickerListener(new DialogPickerTime.OnSelectedNumberPickTimeListener() {
            @Override
            public void onSelectedTimeResult(int type, int duration) {
                String resultTimeString = MediaUtils.getMillSecToHMS(duration);
                switch (type){
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

        if(mStartTime != 0){
            mRangeSeekBar.setRangeValues(1000, duration);

            String startTime = MediaUtils.getMillSecToHMS(mStartTime);
            mStartTimeEditText.setText(startTime);
            mYouTubePlayer.seekToMillis(mStartTime);
            mRangeSeekBar.setSelectedMinValue(mStartTime);

            String endTime = MediaUtils.getMillSecToHMS(mEndTime);
            mEndTimeEditText.setText(endTime);
            mRangeSeekBar.setSelectedMaxValue(mEndTime);
        }else{
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
        mYouTubePlayer.setShowFullscreenButton(false);
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
     * 메뉴 보이기
     */
    private void showMenu() {
        if (!mLock) {
            mBottomSettingLay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 메뉴 숨기기
     */
    private void hideMenu() {
        mBottomSettingLay.setVisibility(View.GONE);
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

            mBottomSettingLay.setVisibility(View.GONE);
            mHelpButton.setVisibility(View.GONE);
            mSearchButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mPlayListButton.setVisibility(View.GONE);
        } else {
            mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

            mBottomSettingLay.setVisibility(View.VISIBLE);
            mHelpButton.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            mBackButton.setVisibility(View.VISIBLE);
            mPlayListButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 영상 재생
     *
     * @param id
     */
    private void startPlay(String id) {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(id);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mYouTubePlayer.pause();
//                }
//            }, 1000);
        }
    }

    private void startAutoPlay(String videoId){
        if(mYouTubePlayer != null){
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
                mYouTubePlayer.pause();
                DialogHelp dialogHelp = new DialogHelp(this, R.style.custom_dialog_fullScreen);
                dialogHelp.show();
                break;
            case R.id.player_search_button:
                finish();
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.player_back_button:
                finish();
                break;
            case R.id.player_screen_orientation_button:
                if(!mInvitationState){
                    showProDialog();
                    return;
                }

                if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mTopCountTextView.setVisibility(View.VISIBLE);
                    hideMenu();
                } else if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mTopCountTextView.setVisibility(View.GONE);
                    showMenu();
                }
                break;

            case R.id.player_top_lock_button:
                mLock = !mLock;
                setLockButtonRes();
                setLock();
                break;

            case R.id.player_top_playlist_button:
                mDialogPlayList = new DialogPlayList(this, R.style.custom_dialog_fullScreen);
                mDialogPlayList.setOnClickListener(new DialogPlayList.OnClickDialogPlayListListener() {
                    @Override
                    public void onPlay(PlayListData data) {
                        mDialogPlayList.dismiss();
                        mPlayId = data.getVideoId();
                        mPlayType = getPlayType();
                        mStartTime = Integer.parseInt(data.getStartTime());
                        mEndTime = Integer.parseInt(data.getEndTime());
                        if(SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY)){
                            startAutoPlay(mPlayId);
                        }else{
                            startPlay(mPlayId);
                        }
                    }
                });
                mDialogPlayList.setOnClickControllerListener(new DialogPlayList.OnClickDialogControllerListener() {
                    @Override
                    public void play(View v) {
                        if(mYouTubePlayer.isPlaying()){
                            mYouTubePlayer.pause();
                            v.setBackgroundResource(R.drawable.ic_play_arrow_gray_24dp);
                        }else{
                            mYouTubePlayer.play();
                            v.setBackgroundResource(R.drawable.ic_pause_gray_24dp);
                        }
                    }
                });
                mDialogPlayList.show();
                refreshPlayListController();
                break;

            case R.id.player_setting_playlist_button:
                if(!mInvitationState){
                    showProDialog();
                    return;
                }

                if(mSnippet != null){
                    String img_url = mSnippet.getThumbnails().getMedium().getUrl();
                    String title = mSnippet.title;
                    long duration = mYouTubePlayer.getDurationMillis();
                    mPlayListArray = mPlayListDataManager.loadPlayList();

                    PlayListData firstPlayListData = new PlayListData();
                    firstPlayListData.setImg_url(img_url);
                    firstPlayListData.setTitle(title);
                    firstPlayListData.setDuration(String.valueOf(duration));
                    firstPlayListData.setVideoId(mPlayId);
                    firstPlayListData.setStartTime(String.valueOf(mStartTime));
                    firstPlayListData.setEndTime(String.valueOf(mEndTime));
                    firstPlayListData.setRepeat(String.valueOf(mRepeatCount));
                    mPlayListArray.add(0, firstPlayListData);

//                    mPlayListDataManager.insert(img_url, title, String.valueOf(duration), mPlayId, String.valueOf(mStartTime), String.valueOf(mEndTime), String.valueOf(mRepeatCount));
                    mPlayListDataManager.insertAllList(mPlayListArray);
                    mPlayListButton.performClick();
                    Toast.makeText(this, getResources().getString(R.string.playlist_add), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.player_setting_repeat_button:
                DialogPickerCount dialogPickerCount = new DialogPickerCount(this, R.style.custom_dialog_fullScreen);
                dialogPickerCount.setOnSelectedNumberPickerListener(onSelectedNumberPickerListener);
                dialogPickerCount.show();
                break;

            case R.id.player_top_popup_button:
//                mPlayListArray = mPlayListDataManager.loadPlayList();
//                PlayListData currentData = createPlayListData();
//                mPlayListArray.add(0, currentData);
//                startOverlayWindowService(this);

                intent = new Intent(this, LoadingActivity.class);
                startActivityForResult(intent, REQ_CODE_AD_FINISH);
                break;
        }
    }

    private DialogPickerCount.OnSelectedNumberPickerListener onSelectedNumberPickerListener = new DialogPickerCount.OnSelectedNumberPickerListener() {
        @Override
        public void onSelectedValue(int value) {
            if(CommonUserData.sMaxRepeatCount < value){
                showProDialog();
                return;
            }
            mRepeatCount = value;

            mExpandableCountTextView.setText(String.valueOf(mRepeatCount));
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

            if(mYouTubePlayer != null){
                mYouTubePlayer.seekToMillis(mStartTime);
            }else{
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
                Log.d(TAG, "timer detected...");

                long currentTime = mYouTubePlayer.getCurrentTimeMillis();
                repeatPlayRange(currentTime);
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 1, 10);
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
    private void savePlayingData(){
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
        Log.d(TAG, "currentTime : " + currentTime + " endTime : " + mEndTime);

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
                mYouTubePlayer.seekToMillis(mStartTime);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExpandableCountTextView.setText(String.valueOf(mRepeatCount));
//                        mRepeatCountEditText.setText(String.valueOf(mRepeatCount));
                        mTopCountTextView.setText(String.valueOf(mRepeatCount));

                        boolean autuplayState = SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_AUTOPLAY);
                        if(autuplayState){
                            Log.d(TAG, "자동 플레이가 설정되어 있습니다.");

                            mPlayType = getPlayType();
                            if(isNext()){
                                Log.d(TAG, "다음 영상이 존재합니다.");
                                nextPlay();
                            }else{
                                Log.d(TAG, "다음 영상이 존재하지 않습니다. 다시 처음부터 재생합니다.");
                                //전체 반복 설정시 처음부터 다시 반복
//                                boolean allRepeatState = SharedPreferencesUtils.getBoolean(PlayerActivity.this, CommonSharedPreferencesKey.KEY_ALLREPEAT);
//                                if(allRepeatState){

                                if(mPlayListArray.size() != 0){
                                    mPlayType = TYPE_NORMAL;
                                    nextPlay();
                                }
//                                }else{
//                                    refreshPlayListController();
//                                }
                            }
                        }else{
                            Log.d(TAG, "자동 플레이가 지정되어 있지 않습니다.");
                        }
                    }
                });
            }
        }
    }

    /**
     * 전체영상 반복재생 (full repeat play)
     */
    private void repeatPlayFull() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.pause();
            mYouTubePlayer.seekToMillis(1000);
            mYouTubePlayer.play();
        }
    }

    /**
     * State Callback Listener
     */
    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying...");
        if (mStartTime == mEndTime) {
            mYouTubePlayer.pause();
            return;
        }
        savePlayingData();
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
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
        stopService(new Intent(this, FloatingService.class));
        super.onDestroy();
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped...");
    }

    @Override
    public void onBackPressed() {
        finish();
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
                if(mYouTubePlayer != null){
                    try{
                        mYouTubePlayer.play();
                    }catch(IllegalStateException e){
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
    private void loadVideos(String id) {
        mCallVideos = mService.getYoutubeVideos("snippet,contentDetails,statistics", id, CommonApiKey.KEY_API_YOUTUBE);
        mCallVideos.enqueue(callback);
    }

    /**
     * 친구초대 유무를 확인하고 그에 맞는 최대 반복 횟수를 세팅한다.
     */
    private void initInviteItem(){
        mInvitationState = SharedPreferencesUtils.getBoolean(this, CommonSharedPreferencesKey.KEY_INVITATION);

        if(mInvitationState){
            CommonUserData.sMaxRepeatCount = CommonUserData.COUNT_MAX;
        }else {
            CommonUserData.sMaxRepeatCount = CommonUserData.COUNT_DEFAULT;
        }
    }

    //PlayList Play
    private int getPlayType(){
        mPlayListArray = mPlayListDataManager.loadPlayList();
        int index = getPlayIndex(mPlayId);

        if (index == -1) {
            return TYPE_NORMAL;
        }else{
            return TYPE_PLAYLIST;
        }
    }

    private boolean isNext(){
        mPlayListArray = mPlayListDataManager.loadPlayList();
        mPlayIndex = getPlayIndex(mPlayId);
        if(mPlayType == TYPE_NORMAL){
            if(mPlayListArray.size() != 0){
                return true;
            }else{
                return false;
            }
        }else if(mPlayType == TYPE_PLAYLIST){
            if(mPlayListArray.size() > mPlayIndex + 1){
                return true;
            }else{
                return false;
            }
        }

        Log.d(TAG, "isNext : 그 어떤 조건도 찾지 못했습니다.");
        return false;
    }

    /**
     * 다음 영상을 재생합니다.
     */
    private void nextPlay(){
        Log.d(TAG, "다음 영상을 재생합니다.");

        if(mPlayType == TYPE_NORMAL){
            mPlayType = TYPE_PLAYLIST;
            mCurrentPlayListData = mPlayListArray.get(0);
            mPlayId = mCurrentPlayListData.getVideoId();
            mStartTime = Integer.parseInt(mCurrentPlayListData.getStartTime());
            mEndTime = Integer.parseInt(mCurrentPlayListData.getEndTime());
            //            mRepeatCount = Integer.parseInt(mCurrentPlayListData.getRepeat()); //개별반복재생 다음버전에서 개발... 고민 필요
            startAutoPlay(mPlayId);
            refreshPlayListController();
        }else if(mPlayType == TYPE_PLAYLIST){
            Log.d(TAG, "PlayIndex : " + mPlayIndex);
            mCurrentPlayListData = mPlayListArray.get(mPlayIndex + 1);
            mPlayId = mCurrentPlayListData.getVideoId();
            mStartTime = Integer.parseInt(mCurrentPlayListData.getStartTime());
            mEndTime = Integer.parseInt(mCurrentPlayListData.getEndTime());
            //            mRepeatCount = Integer.parseInt(mCurrentPlayListData.getRepeat());
            startAutoPlay(mPlayId);
            refreshPlayListController();
        }
    }

    /**
     * 이전 영상을 재생합니다.
     */
    private void prevPlay(){

    }

    /**
     * 현재 재생되고 있는 영상의 인덱스 위치를 탐색한다.
     */
    private int getPlayIndex(String videoId){
        int index = -1;

        for(int i=0; i<mPlayListArray.size(); i++){
            if(mPlayListArray.get(i).getVideoId().equals(videoId)){
                index = i;
                break;
            }
        }
        Log.d(TAG, "Index Search Detected : " + index);
        return index;
    }

    //PlayList Controller
    /**
     * PlayList의 컨트롤러를 업데이트 한다.
     */
    private void refreshPlayListController(){
        if(mDialogPlayList != null){
            if(mDialogPlayList.isShowing()){
                if(mPlayType == TYPE_NORMAL){
                    if(mSnippet != null){
                        String thumbUrl = mSnippet.getThumbnails().getMedium().getUrl();
                        String title = mSnippet.title;
                        mDialogPlayList.refreshController(thumbUrl, title, mYouTubePlayer.isPlaying());
                    }
                }else if(mPlayType == TYPE_PLAYLIST){
                    if(mCurrentPlayListData != null){
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
            try{
                result = response.body().toString();
            }catch(NullPointerException e){
                return;
            }

            Log.d(TAG, "onResponse...");
            if (call == mCallVideos) {
                Videos videos = (Videos) mGson.fromJson(result, Videos.class);

                Videos.PageInfo pageInfo = videos.pageInfo;
                String totalResults = pageInfo.totalResults;
                if(totalResults.equals("0")){
                    Toast.makeText(PlayerActivity.this, getResources().getString(R.string.error_videos_emptyInfo), Toast.LENGTH_SHORT);
                }else{
                    if(videos != null){
                        ArrayList<Videos.Item> items = videos.items;
                        if(items != null){
                            if(items.size() != 0){
                                mSnippet = videos.items.get(0).snippet;
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
    public void showProDialog(){
        if(mYouTubePlayer.isPlaying()){
            mYouTubePlayer.pause();
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

                if(intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialogPro.show();
    }

    //Overlay
    public void startOverlayWindowService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(context)) {
            onObtainingPermissionOverlayWindow();
        } else {
            Intent intent = new Intent(this, FloatingService.class);
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
            }
            startService(intent);
            goHome();
        }
    }

    public void onObtainingPermissionOverlayWindow() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    private PlayListData createPlayListData() {
        String img_url = mSnippet.getThumbnails().getMedium().getUrl();
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

    public void goHome(){
        Intent intent = new Intent(Intent.ACTION_MAIN); //태스크의 첫 액티비티로 시작
        intent.addCategory(Intent.CATEGORY_HOME);   //홈화면 표시
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //새로운 태스크를 생성하여 그 태스크안에서 액티비티 추가
        startActivity(intent);
    }

    private void showPopupFlaotingWindow(){
        mPlayListArray = mPlayListDataManager.loadPlayList();
        PlayListData currentData = createPlayListData();
        mPlayListArray.add(0, currentData);
        startOverlayWindowService(this);
    }
}
