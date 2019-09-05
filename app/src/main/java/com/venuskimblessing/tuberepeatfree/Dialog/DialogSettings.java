package com.venuskimblessing.tuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ViewGroup;

import com.venuskimblessing.tuberepeatfree.R;

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
