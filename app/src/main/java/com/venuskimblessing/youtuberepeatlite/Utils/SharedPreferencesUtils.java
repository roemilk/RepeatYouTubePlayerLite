package com.venuskimblessing.youtuberepeatlite.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kim YoungHun on 2016-06-02.
 * SharedPreferences를 사용하기 편리하게 가공한 클래스. set과 get의 작업이 편리하다.
 */
public class SharedPreferencesUtils {
    private static final String SharedPreferences_Name = "Setting";

    private static final String DEFAULT_VALUE_STRING    = "";
    private static final boolean    DEFAULT_VALUE_BOOLEAN   = false;
    private static final int        DEFAULT_VALUE_INT       = 0;
    private static final long       DEFAULT_VALUE_LONG      = 0L;
    private static final float      DEFAULT_VALUE_FLOAT     = 0F;

    private SharedPreferencesUtils() {}

    /**
     * SharedPreferences 객체를 리턴한다.
     * @param context
     * @return SharedPreferences
     */
    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SharedPreferences_Name, Context.MODE_PRIVATE);
    }

    public static void setBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setInt(Context context, String key, int value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setFloat(Context context, String key, float value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void setLong(Context context, String key, long value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setString(Context context, String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key){
        return getSharedPreferences(context).getBoolean(key, DEFAULT_VALUE_BOOLEAN);
    }

    public static int getInt(Context context, String key){
        return getSharedPreferences(context).getInt(key, DEFAULT_VALUE_INT);
    }

    public static float getFloat(Context context, String key){
        return getSharedPreferences(context).getFloat(key, DEFAULT_VALUE_FLOAT);
    }

    public static long getLong(Context context, String key){
        return getSharedPreferences(context).getLong(key, DEFAULT_VALUE_LONG);
    }

    public static String getString(Context context, String key){
        return getSharedPreferences(context).getString(key, DEFAULT_VALUE_STRING);
    }
}
