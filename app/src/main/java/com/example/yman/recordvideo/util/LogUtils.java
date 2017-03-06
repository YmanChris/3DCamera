package com.example.yman.recordvideo.util;

import android.util.Log;

/**
 * Created by yinxiangyang on 2017/2/17.
 */

public class LogUtils {
    public static boolean debug = true;

    public static void e(String tag,String msg){
        if(debug){
            Log.e(tag,msg);
        }
    }
    public static void i(String tag,String msg){
        if(debug){
            Log.i(tag,msg);
        }

    }
    public static void w(String tag,String msg){
        if(debug){
            Log.w(tag,msg);
        }

    }
    public static void d(String tag,String msg){
        if(debug){
            Log.d(tag,msg);
        }
    }
    public static void v(String tag,String msg){
        if(debug){
            Log.v(tag,msg);
        }
    }

    public static void setDebug(boolean debug) {
        LogUtils.debug = debug;
    }
}
