package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.venuskimblessing.tuberepeatfree.R;

public class DialogPro extends Dialog {

    private TextView mTitleTextView, mContentTextView;
    private Button mProButton, mInvitationButton, mCancelButton;
    private View.OnClickListener listener = null;

    public DialogPro(@NonNull Context context) {
        super(context);
        init();
    }

    public DialogPro(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public DialogPro(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_pro);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitleTextView = (TextView)findViewById(R.id.dialog_pro_title_textView);
        mContentTextView = (TextView)findViewById(R.id.dialog_pro_description_textView);

        mCancelButton = (Button)findViewById(R.id.dialog_cancel_button);
        mProButton = (Button)findViewById(R.id.dialog_pro_button);
        mInvitationButton = (Button)findViewById(R.id.dialog_invite_button);
    }

    public void setDisableInvitationButton(){
        mInvitationButton.setVisibility(View.GONE);
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.listener = onClickListener;
        if(listener != null){
            mCancelButton.setOnClickListener(listener);
            mProButton.setOnClickListener(listener);
            mInvitationButton.setOnClickListener(listener);
        }
    }
}
