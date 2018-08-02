package com.venuskimblessing.youtuberepeatlite.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class OSUtils {
    public static final String TAG = "OSUtils";

    /**
     * 패키지 정보를 반환합니다.
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context){
        PackageInfo pi = null;
        try{
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }catch(PackageManager.NameNotFoundException e){
            Log.d(TAG, "패키지 정보를 불러오지 못하였습니다.");
        }
        return pi;
    }
}
