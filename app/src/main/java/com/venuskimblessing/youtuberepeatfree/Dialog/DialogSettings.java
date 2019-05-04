package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.venuskimblessing.youtuberepeatfree.R;

public class DialogSettings extends Dialog {

    public DialogSettings(@NonNull Context context) {
        super(context);
        init();
    }

    public DialogSettings(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public DialogSettings(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_settings);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
