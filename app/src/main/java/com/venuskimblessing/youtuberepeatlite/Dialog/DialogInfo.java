package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.venuskimblessing.youtuberepeatlite.Json.Videos;
import com.venuskimblessing.youtuberepeatlite.LoadingActivity;
import com.venuskimblessing.youtuberepeatlite.Player.PlayerActivity;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.Utils.FormatUtils;
import com.venuskimblessing.youtuberepeatlite.Utils.MediaUtils;

public class DialogInfo extends Dialog implements View.OnClickListener {
    public static final String TAG = "DialogInfo";

    private RelativeLayout mParentLay;
    private ImageView mThumbImageView;
    private TextView mTitleTextView, mDescriptionTextView, mDurationTextView, mViewCountTextView, mChannelTitleTextView;;
    private Button mPlayButton;

    private Context mContext;
    private String id;

    private View.OnClickListener listener;

    public DialogInfo(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogInfo(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    public DialogInfo(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_info);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mParentLay = (RelativeLayout)findViewById(R.id.dialog_info_parent_lay);
        mThumbImageView = (ImageView)findViewById(R.id.dialog_info_thumb_imageView);
        mPlayButton = (Button)findViewById(R.id.dialog_info_play_button);
        mTitleTextView = (TextView)findViewById(R.id.dialog_info_title_textView);
        mDescriptionTextView = (TextView)findViewById(R.id.dialog_info_description_textView);
        mDurationTextView = (TextView)findViewById(R.id.dialog_info_duration_textView);
        mViewCountTextView = (TextView)findViewById(R.id.dialog_info_viewCount_textView);
        mChannelTitleTextView = (TextView)findViewById(R.id.dialog_info_channelTitle_textView);

        mParentLay.setOnClickListener(this);
        mThumbImageView.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
    }

    public void setData(Videos data, String thumbUrl){
        if(data != null){
            Videos.Item item = data.items.get(0);
            this.id = item.id;

            Glide.with(getContext()).load(thumbUrl)
                    .thumbnail(0.1f)
                    .into(mThumbImageView);


            Videos.ContentDetails contentDetails = item.contentDetails;
            String durationString = null;
            if(contentDetails != null){
                durationString = contentDetails.duration;
            }

            int duration = 0;
            if(durationString != null){
                duration = (int)MediaUtils.getDuration(durationString);
            }

            String durationResult = MediaUtils.getMillSecToHMS(duration);
            if(durationResult != null){
                mDurationTextView.setText(durationResult);
            }

            Videos.Snippet snippet = item.snippet;
            mTitleTextView.setText(snippet.title);
            mDescriptionTextView.setText(snippet.description);
            mChannelTitleTextView.setText(snippet.channelTitle);

            Videos.statistics statistics = item.statistics;
            if(statistics != null){
                String viewCount = statistics.viewCount;
                String resultViewcount = "";
                if(viewCount != null && !viewCount.equals("")){
                    resultViewcount = FormatUtils.parseNumberFormat(viewCount);
                }else{
                    resultViewcount = "???";
                }
                mViewCountTextView.setText(resultViewcount);
            }
        }
    }

    public void setListener(View.OnClickListener onClickListener){
        this.listener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_info_parent_lay:
                dismiss();
                break;
            case R.id.dialog_info_thumb_imageView:
            case R.id.dialog_info_play_button:
                v.setTag(id);
                listener.onClick(v);
                break;
        }
    }
}
