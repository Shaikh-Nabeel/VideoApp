package com.example.videoapp;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class MyVideoAppApplication extends Application {
    private static Context context;

    public static Context getAppContext() {
        return MyVideoAppApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }
}
