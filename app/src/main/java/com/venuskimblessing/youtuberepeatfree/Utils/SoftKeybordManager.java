package com.venuskimblessing.youtuberepeatfree.Utils;

import android.view.View;
import android.view.Window;

public class SoftKeybordManager {

    private Window mWindow;
    private View decoView;

    public SoftKeybordManager(Window window) {
        this.mWindow = window;
    }

    /**
     * 소프트키를 숨김처리합니다.
     */
    public void hideSoftKeyInvisible() {
        decoView = mWindow.getDecorView();
        decoView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    int uiOption = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    decoView.setSystemUiVisibility(uiOption);
                }
            }
        });
    }
}
