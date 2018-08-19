package com.venuskimblessing.youtuberepeatlite.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.venuskimblessing.youtuberepeatlite.Common.CommonSharedPreferencesKey;
import com.venuskimblessing.youtuberepeatlite.Common.CommonUserData;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.Utils.SharedPreferencesUtils;

public class DialogInvitation extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogInvitation";

    private Context mContext;
    private RelativeLayout mParentLay;
    private TextView mInviteContentTextView, mInvitationCountTextView;
    private Button mInvitationButton;

    private View.OnClickListener listener = null;

    public DialogInvitation(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogInvitation(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogInvitation(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_invitation);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mParentLay = (RelativeLayout)findViewById(R.id.dialog_invitation_parent_lay);
        mInviteContentTextView = (TextView)findViewById(R.id.dialog_invitation_tip_content_textView);
        mInvitationCountTextView = (TextView)findViewById(R.id.dialog_invitation_count_textView);
        mInvitationButton = (Button)findViewById(R.id.dialog_invitation_button);

        int invitationCount = SharedPreferencesUtils.getInt(mContext, CommonSharedPreferencesKey.KEY_INVITATION_COUTN);
        Resources res = mContext.getResources();
        mInvitationCountTextView.setText(res.getString(R.string.invitation_count) + " " + invitationCount + " Friends");

        if(invitationCount >= CommonUserData.INVITE_COUNT_COMPLETE){
            mInviteContentTextView.setText(res.getString(R.string.invitation_complete));
        }

        mParentLay.setOnClickListener(this);
        mInvitationButton.setOnClickListener(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        if(onClickListener != null){
            listener = onClickListener;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_invitation_parent_lay:
                dismiss();
                break;
            case R.id.dialog_invitation_button:
                dismiss();
                listener.onClick(v);
                break;
        }
    }
}
