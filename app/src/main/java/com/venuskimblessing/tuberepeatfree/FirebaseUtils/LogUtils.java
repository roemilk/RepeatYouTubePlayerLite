package com.venuskimblessing.tuberepeatfree.FirebaseUtils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * 파이어베이스 로그 관련 클래스
 */
public class LogUtils {
    /**
     * Firebase Log 기록 이벤트
     * @param context
     * @param eventName
     * @param bundle
     */
    public static void logEvent(Context context, String eventName, Bundle bundle){
       FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(eventName, bundle);
    }
}
