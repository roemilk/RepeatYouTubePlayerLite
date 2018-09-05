package com.venuskimblessing.youtuberepeatlite.Utils;

import android.util.Log;

import java.text.DecimalFormat;

public class FormatUtils {
    private static final String TAG = "FormatUtils";
    /**
     *
     * 요약 : 숫자를 3자리 콤마표시하여 리턴한다.
     * @return
     */
    public static String parseNumberFormat(String s) {
        Log.d(TAG, s);
        long inValues = Long.parseLong(s);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result = (String)Commas.format(inValues);
        return result;
    }
}
