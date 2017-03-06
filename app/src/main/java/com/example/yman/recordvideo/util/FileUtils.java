package com.example.yman.recordvideo.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.example.yman.recordvideo.model.VideoInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinxiangyang on 2017/1/24.
 */

public class FileUtils {
    private static final  String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "com.jd.record";

    /**
     * 初始化文件路径
     * @return
     */
    public static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdirs();
            }
        }
        return storagePath;
    }

    /**
     * 获取视频列表
     * @return
     */
    public static List<VideoInfo> getVideoList(){

        List<VideoInfo> videoInfos = new ArrayList<>();
        MediaMetadataRetriever media = new MediaMetadataRetriever();

        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
        }
        File f = new File(storagePath);
        File[] lists = f.listFiles();
        if(lists != null && lists.length > 0) {
            for (File file : lists) {
                String filename = file.getName();
                if (filename.trim().toLowerCase().endsWith(".mpg")) {
                    media.setDataSource(storagePath + "/" + filename);
                    Bitmap bitmap = media.getFrameAtTime();
                    filename = filename.split(".mpg")[0];
                    videoInfos.add(new VideoInfo(filename, bitmap));
                }
            }
        }
        return videoInfos;
    }

    /**
     * 创建sku文件
     * @param sku
     */
    public static void createNewFile(String sku){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
        }
        File file = new File(storagePath+ "/" + sku);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static boolean checkImageExists(String sku){
        File file = new File(getStoragePath() + "/" + sku);
        if(file.exists()){
            if(file.isDirectory()){
                if(file.listFiles() != null && file.listFiles().length > 0)
                    return true;
                else
                    return false;
            }
            else return false;
        }
        else
            return false;
    }

    /**
     * 获取路径
     * @return
     */
    public static String getStoragePath() {
        return storagePath;
    }
}
