package com.wulinpeng.weicommunity;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.OutOfMemoryError;

/**
 * @author wulinpeng
 * @datetime: 16/10/16 下午7:21
 * @description:
 */
public class WeiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).schemaVersion(0).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

}
