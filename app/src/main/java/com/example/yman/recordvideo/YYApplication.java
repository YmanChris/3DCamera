package com.example.yman.recordvideo;

import android.app.Application;

import com.example.yman.recordvideo.util.FileUtils;
import com.example.yman.recordvideo.util.LogUtils;

/**
 * Created by Think on 2017/1/24.
 */

public class YYApplication extends Application{
    private static YYApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        FileUtils.initPath();
        LogUtils.setDebug(true);
    }
    public void setInstance(YYApplication application){
        instance = application;
    }

    public static YYApplication getInstance() {
        return instance;
    }
}
