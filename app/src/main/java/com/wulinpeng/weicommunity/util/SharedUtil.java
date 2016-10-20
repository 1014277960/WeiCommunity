package com.wulinpeng.weicommunity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * @author wulinpeng
 * @datetime: 16/10/18 下午8:16
 * @description:
 */
public class SharedUtil {

    public static void putLong(Context context, String key, long l) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, l);
        editor.commit();
        editor.apply();
    }

    public static long getLong(Context context, String key, long def) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key, def);
    }
}
