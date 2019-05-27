package com.venuskimblessing.youtuberepeatfree.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.venuskimblessing.youtuberepeatfree.R;
import com.venuskimblessing.youtuberepeatfree.Utils.SoftKeybordManager;

public class DialogBatterySaving extends Dialog {
    public static final String TAG = "DialogPickerCount";

    private Context mContext;
    private SoftKeybordManager mSoftKeybordManager;

    public DialogBatterySaving(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DialogBatterySaving(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    protected DialogBatterySaving(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.layout_dialog_batterysaving);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSoftKeybordManager = new SoftKeybordManager(getWindow());
        mSoftKeybordManager.hideSoftKeyInvisible();
    }
}
