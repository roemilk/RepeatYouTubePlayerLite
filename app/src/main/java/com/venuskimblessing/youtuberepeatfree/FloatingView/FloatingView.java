package com.venuskimblessing.youtuberepeatfree.FloatingView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.ui.PlayerUIController;
import com.venuskimblessing.youtuberepeatfree.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatfree.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.SearchActivity;
import com.venuskimblessing.youtuberepeatfree.Utils.MediaUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.SharedPreferencesUtils;
import com.venuskimblessing.youtuberepeatfree.Utils.UIConvertUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class FloatingView extends RelativeLayout {
    private final String TAG = "FloatingView";

    private Context mContext;
    public RelativeLayout mRootLay;
    private TouchEventLayout mTouchLay;
    private YouTubePlayer mYouTubePlayer;
    private YouTubePlayerView mYouTubePlayerView;
    private PlayerUIController mPlayerUIController;

    //Repeat
    private boolean mAutoPlay = false;
    private int mCurrentTime = 0;
    private int mStartTime = 0; //시작시간
    private int mEndTime = 0; //끝시간
    private int mRepeatCount = 0; //반복횟수
    private boolean mRepeatInfinite = false; //무한반복

    //PlayData
    private PlayListData mPlayListData;
    private ArrayList<PlayListData> mPlayListDataArrayList;
    private int mPlayIndex = 0;

    //PlayList Iterator
    private ListIterator<PlayListData> mPlayListIterator = null;

    //Controller
    private ImageView mExitImageView, mZoomImageView;
    private TextView mTimeTextView, mCountTextView;
    private OnTouchListener mListener;

    //Screen BroadcastReciver
    private ScreenOffOnReciver mScreenOffOnReciver = new ScreenOffOnReciver();
    private IntentFilter mFilterScreenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);

    public FloatingView(Context context, OnTouchListener mViewTouchListener) {
        super(context);
        this.mContext = context;
        mListener = mViewTouchListener;
        init();
    }

    public FloatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public FloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        register();
        mAutoPlay = SharedPreferencesUtils.getBoolean(mContext, CommonSharedPreferencesKey.KEY_AUTOPLAY);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(infService);
        View v = li.inflate(R.layout.layout_floatingview, this, false);
        addView(v);

        mRootLay = (RelativeLayout) v.findViewById(R.id.floating_root_view);
        mTouchLay = new TouchEventLayout(mContext, mListener);
        mYouTubePlayerView = new YouTubePlayerView(mContext);

        mTouchLay.addView(mYouTubePlayerView);
        mRootLay.addView(mTouchLay);

        mYouTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer youTubePlayer) {
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        super.onReady();
                        mYouTubePlayer = youTubePlayer;
                        mYouTubePlayer.addListener(youTubePlayerListener);
                        customMenu();

                        if (mAutoPlay) {
                            startPlayList(mPlayIndex);
                        } else {
                            startPlay(mPlayListData);
                        }
                    }
                });
            }
        }, true);
    }

    private YouTubePlayerListener youTubePlayerListener = new YouTubePlayerListener() {
        @Override
        public void onReady() {
            Log.d(TAG, "onReady..");

        }

        @Override
        public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
            if (state == PlayerConstants.PlayerState.PLAYING) {
                Log.d(TAG, "PLAYING");
                if (mStartTime == mEndTime) {
                    mYouTubePlayer.pause();
                    return;
                }
            } else if (state == PlayerConstants.PlayerState.PAUSED) {
                Log.d(TAG, "PAUSED");
            } else if (state == PlayerConstants.PlayerState.ENDED) {
                Log.d(TAG, "ENDED");
                play();
            }
        }

        @Override
        public void onPlaybackQualityChange(@NonNull PlayerConstants.PlaybackQuality playbackQuality) {

        }

        @Override
        public void onPlaybackRateChange(@NonNull PlayerConstants.PlaybackRate playbackRate) {

        }

        @Override
        public void onError(@NonNull PlayerConstants.PlayerError error) {

        }

        @Override
        public void onApiChange() {

        }

        @Override
        public void onCurrentSecond(float second) {
//            Log.d(TAG, "second : " + second);
            mCurrentTime = (int) second;
            repeatPlayRange(mCurrentTime);
        }

        @Override
        public void onVideoDuration(float duration) {

        }

        @Override
        public void onVideoLoadedFraction(float loadedFraction) {
//            Log.d(TAG, "onVideoLoadedFraction..");
        }

        @Override
        public void onVideoId(@NonNull String videoId) {

        }
    };

    public void setPlayData(PlayListData playData) {
        this.mPlayListData = playData;
    }

    public void setRepeatCount(int repeatCount) {
        this.mRepeatCount = repeatCount;
    }

    public void setRepeatInfinite(boolean infinite) {
        this.mRepeatInfinite = infinite;
    }

    public void setPlayListData(ArrayList<PlayListData> playListDataArrayList, int startIndex) {
        this.mPlayListDataArrayList = playListDataArrayList;
        this.mPlayIndex = startIndex;
    }

    private void printAllPlayListData(){
        for(PlayListData data : mPlayListDataArrayList){
            Log.d(TAG, "data title : " + data.getTitle());
        }
    }

    private void initTime(PlayListData data) {
        mStartTime = (Integer.parseInt(data.getStartTime()) / 1000);
        mEndTime = (Integer.parseInt(data.getEndTime()) / 1000);

        mTimeTextView.setText(MediaUtils.getSecToHMS(mStartTime) + " - " + MediaUtils.getSecToHMS(mEndTime));
    }

    private void updateRepeatCount() {
        if (mRepeatInfinite) {
            String infiniteString = mContext.getResources().getString(R.string.dialog_numberpick_infinite);
            mCountTextView.setText(infiniteString);
        } else {
            mCountTextView.setText("R" + String.valueOf(mRepeatCount));
        }
    }

    private void customMenu() {
        mPlayerUIController = mYouTubePlayerView.getPlayerUIController();
        mPlayerUIController.showYouTubeButton(false);
        mPlayerUIController.showFullscreenButton(false);

        int width = (int) UIConvertUtils.convertDpToPixel(34, mContext);
        int height = (int) UIConvertUtils.convertDpToPixel(34, mContext);

        mExitImageView = new ImageView(mContext);
        mExitImageView.setImageResource(R.drawable.ic_exit_white_24dp);
        LinearLayout.LayoutParams exitParams = new LinearLayout.LayoutParams(width, height);
        mExitImageView.setLayoutParams(exitParams);
        mExitImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                unRegister();
                Intent intent = new Intent(mContext, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        mPlayerUIController.addView(mExitImageView);

        mZoomImageView = new ImageView(mContext);
        mZoomImageView.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
        LinearLayout.LayoutParams zoomParmas = new LinearLayout.LayoutParams(width, height);
        mZoomImageView.setLayoutParams(zoomParmas);
        mZoomImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleScreenSize(mZoomImageView);
            }
        });
        mPlayerUIController.addView(mZoomImageView);

        mTimeTextView = new TextView(mContext);
        int marginRight = (int) UIConvertUtils.convertDpToPixel(10, mContext);
        LinearLayout.LayoutParams timeTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeTextParams.rightMargin = marginRight;
        timeTextParams.gravity = Gravity.CENTER_VERTICAL;
        mTimeTextView.setLayoutParams(timeTextParams);

        mTimeTextView.setTextColor(Color.WHITE);
        mPlayerUIController.addView(mTimeTextView);

        mCountTextView = new TextView(mContext);
        LinearLayout.LayoutParams countTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        countTextParams.rightMargin = marginRight;
        countTextParams.gravity = Gravity.CENTER_VERTICAL;
        mCountTextView.setLayoutParams(countTextParams);
        mCountTextView.setTextColor(Color.WHITE);
        mPlayerUIController.addView(mCountTextView);
    }

    /**
     * 스크린 사이즈 변경
     *
     * @param imageView
     */
    private void toggleScreenSize(ImageView imageView) {
        if (imageView.isSelected()) {
            imageView.setSelected(false);
            imageView.setImageResource(R.drawable.ic_fullscreen_exit_white_24dp);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mYouTubePlayerView.getLayoutParams();
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            mYouTubePlayerView.setLayoutParams(params);

            //visible controller
            mExitImageView.setVisibility(View.VISIBLE);
            mTimeTextView.setVisibility(View.VISIBLE);
        } else {
            imageView.setSelected(true);
            imageView.setImageResource(R.drawable.ic_fullscreen_white_24dp);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mYouTubePlayerView.getLayoutParams();
            params.width = (int) UIConvertUtils.convertDpToPixel(180, mContext);
            params.width = (int) UIConvertUtils.convertDpToPixel(140, mContext);
            mYouTubePlayerView.setLayoutParams(params);

            //gone controller
            mExitImageView.setVisibility(View.GONE);
            mTimeTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 구간영상 반복재생 (range repeat play)
     *
     * @param currentTime
     */
    private void repeatPlayRange(int currentTime) {
        Log.d(TAG, "currentTime : " + currentTime + " endTime : " + mEndTime);

        if (mRepeatInfinite) { //무한 반복
            if (currentTime >= mEndTime - 1) {
                mYouTubePlayer.seekTo(mStartTime);
                mYouTubePlayer.play();
            }
            return;
        }

        if (mRepeatCount - 1 > 0) {
            if (currentTime >= mEndTime - 1) { //반복 횟수가 설정된 경우
                mYouTubePlayer.seekTo(mStartTime);
                mYouTubePlayer.play();
                mRepeatCount--;

                Log.d(TAG, "repeat count : " + mRepeatCount);
            }
        } else {
            if (currentTime >= mEndTime) { //Repeat End
                if (mRepeatCount > 0) { //영상을 유튜브플레이버튼을 눌러서 시작하는 경우는 타이머의 조건에 포함되지 않아 최초플레이의 경우 RepeatCount 한개가 감소되지 않고 시작하는 문제가 있어 RepeatCount를 표시하는 텍스트뷰와 싱크를 맞추기 위해 마지막 부분에서 감소처리하여 싱크를 맞춘다.
                    mRepeatCount--;
                    Log.d(TAG, "repeat count : " + mRepeatCount);
                }
                mYouTubePlayer.pause();
                mYouTubePlayer.seekTo(mStartTime);

                play();
            }
        }
        updateRepeatCount();
    }

    private void play() {
        if (mAutoPlay) {
            if (checkPlayPossiblePlayIndex()) {
                startPlayList(mPlayIndex);
            } else {
                mPlayIndex = 0;
                startPlayList(mPlayIndex);
            }
        } else {
            Log.d(TAG, "자동 플레이가 지정되어 있지 않습니다.");
            return;
        }
    }

    /**
     * 단일 영상 재생
     */
    private void startPlay(PlayListData data) {
        initTime(data);
        updateRepeatCount();
        String videoId = data.getVideoId();
        if (mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(videoId, mStartTime);
        }
    }

    /**
     * 플레이리스트 영상 재생
     */
    private void startPlayList(int playIndex) {
        PlayListData data = mPlayListDataArrayList.get(playIndex);
        initTime(data);
        String videoId = data.getVideoId();
        if (mYouTubePlayer != null) {
            mYouTubePlayer.loadVideo(videoId, mStartTime);
            mPlayIndex++;
        }
    }

    /**
     * 재생 가능한 인덱스인지 검사
     *
     * @return
     */
    private boolean checkPlayPossiblePlayIndex() {
        if (mPlayIndex < mPlayListDataArrayList.size()) {
            return true; //재생 가능 Index 범위
        } else {
            return false; //재생 불가능 Index 범위
        }
    }

    //BroadcastReciver
    public void register() {
        mContext.registerReceiver(mScreenOffOnReciver, mFilterScreenOff);
    }

    public void unRegister() {
        try {
            mContext.unregisterReceiver(mScreenOffOnReciver);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "unRegister ILLegalArgumentException...");
        }
    }

    public class ScreenOffOnReciver extends BroadcastReceiver {
        private static final String TAG = "ScreenOffOnReciver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "Screen Off..");

                if (mYouTubePlayer != null) {
                    mYouTubePlayer.pause();
                }
            }
        }
    }
}
