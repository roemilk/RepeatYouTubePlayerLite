package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.venuskimblessing.tuberepeatfree.R;
import com.wang.avi.AVLoadingIndicatorView;

public class DialogTimer extends Dialog implements View.OnClickListener {
    private static final String TAG = "DialogEnding";

    private Context mContext;

    public DialogTimer(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogTimer(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogTimer(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.layout_timer);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
