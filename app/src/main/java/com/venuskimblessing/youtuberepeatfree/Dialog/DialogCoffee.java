package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.venuskimblessing.youtuberepeatfree.R;

public class DialogCoffee extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogCommon";

    private Context mContext;
    private RelativeLayout mParentLay;
    private Button mBuyButton;

    private View.OnClickListener listener = null;

    public DialogCoffee(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogCoffee(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogCoffee(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_coffee);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mParentLay = (RelativeLayout)findViewById(R.id.dialog_coffee_parent_lay);
        mBuyButton = (Button)findViewById(R.id.dialog_coffee_button);

        mParentLay.setOnClickListener(this);
        mBuyButton.setOnClickListener(this);
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_coffee_parent_lay:
                dismiss();
                break;
            case R.id.dialog_coffee_button:
                //플레이스토어로 이동
                dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.venuskimblessing.youtuberepeat"));
                mContext.startActivity(intent);
                break;
        }
    }
}
