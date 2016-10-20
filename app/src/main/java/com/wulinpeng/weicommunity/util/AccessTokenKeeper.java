package com.wulinpeng.weicommunity.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * @author wulinpeng
 * @datetime: 16/10/7 下午5:13
 * @description:
 */
public class AccessTokenKeeper {

    private static final String UID = "uid";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String REFRESH_TOKEN = "refresh_token";

    public static void putAccessToken(Context context, Oauth2AccessToken token) {
        if (context == null || token == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UID, token.getUid());
        editor.putString(ACCESS_TOKEN, token.getToken());
        editor.putString(REFRESH_TOKEN, token.getRefreshToken());
        editor.putLong(EXPIRES_IN, token.getExpiresTime());
        editor.commit();
        editor.apply();
    }

    public static Oauth2AccessToken getAccessToken(Context context) {
        if (context == null) {
            return null;
        }
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        token.setUid(sp.getString(UID, ""));
        token.setToken(sp.getString(ACCESS_TOKEN, ""));
        token.setRefreshToken(sp.getString(REFRESH_TOKEN, ""));
        token.setExpiresTime(sp.getLong(EXPIRES_IN, 0));
        return token;
    }

}