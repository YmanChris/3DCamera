package com.example.yman.recordvideo.util;

/**
 * Created by Think on 2017/1/31.
 */

public class StringUtils {
    public static boolean isEmpty(String s){
        if(s == null || s.equals(""))
            return true;
        else
            return false;
    }

    public static boolean isEquals(String s1,String s2){
        return s1.equals(s2);
    }
}
