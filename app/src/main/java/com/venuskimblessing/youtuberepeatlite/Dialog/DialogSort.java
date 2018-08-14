package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.venuskimblessing.youtuberepeatlite.R;

public class DialogSort extends Dialog implements View.OnClickListener {
    public static final String TAG = "DialogHelp";

    private Context mContext = null;
    private Button mDateButton, mRatingButton, mRelevanceButton, mCountButton;
    private View.OnClickListener mListener = null;

    public DialogSort(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogSort(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogSort(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.layout_dialog_sort);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mDateButton = (Button)findViewById(R.id.sort_date_button);
        mRatingButton = (Button)findViewById(R.id.sort_rating_button);
        mRelevanceButton = (Button)findViewById(R.id.sort_relevance_button);
        mCountButton = (Button)findViewById(R.id.sort_count_button);

        mDateButton.setOnClickListener(this);
        mRatingButton.setOnClickListener(this);
        mRelevanceButton.setOnClickListener(this);
        mCountButton.setOnClickListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            this.mListener = onClickListener;
        }
    }

    @Override
    public void onClick(View view) {
        if(mListener != null){
            mListener.onClick(view);
        }
    }
}
