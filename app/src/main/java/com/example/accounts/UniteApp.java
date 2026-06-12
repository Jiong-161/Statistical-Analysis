package com.example.accounts;

import android.app.Application;

import com.example.accounts.db.DBManager;

/** 全局 Application，负责初始化数据库等资源。 */
public class UniteApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化数据库
        DBManager.initDB(getApplicationContext());
    }
}
