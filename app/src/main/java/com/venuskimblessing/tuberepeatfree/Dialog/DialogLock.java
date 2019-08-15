package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.venuskimblessing.tuberepeatfree.R;

public class DialogLock extends Dialog {

    public DialogLock(@NonNull Context context) {
        super(context);
        init();
    }

    public DialogLock(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected DialogLock(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_lock);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
