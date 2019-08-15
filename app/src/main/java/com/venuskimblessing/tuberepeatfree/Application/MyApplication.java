package com.venuskimblessing.tuberepeatfree.Application;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate..");
    }
}
