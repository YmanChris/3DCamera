package com.example.yman.recordvideo.model;

import android.graphics.Bitmap;

/**
 * Created by yinxiangyang on 2017/2/11.
 */

public class VideoInfo {
    public String name;
    public Bitmap bitmap;

    public VideoInfo(String name , Bitmap bitmap){
        this.name = name;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
