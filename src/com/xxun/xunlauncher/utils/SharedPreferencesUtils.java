package com.xxun.xunlauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liaoyi on 2018/9/17.
 */

public class SharedPreferencesUtils {

    private static String PREFERENCE_NAME = "LauncherData";

    public static boolean putDeviceVersion(Context context, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("DeviceVersion", value);
        return editor.commit();
    }

    public static String getDeviceVersion(Context context) {
        return getString(context, "DeviceVersion", null);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }
}
