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
 * Created by Think on 2017/1/24.
 */

public class FileUtils {
    private static final  String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "com.jd.record";

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

    public static List<VideoInfo> getVideoList(){

        List<VideoInfo> videoInfos = new ArrayList<>();
        MediaMetadataRetriever media = new MediaMetadataRetriever();

        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
        }
        File f = new File(storagePath);
        File[] lists = f.listFiles();
        for(File file:lists){
            String filename = file.getName();
            if(filename.trim().toLowerCase().endsWith("mpg")){
                media.setDataSource(storagePath +"/"+ filename);
                Bitmap bitmap = media.getFrameAtTime();
                videoInfos.add(new VideoInfo(filename,bitmap));
            }
        }
        return videoInfos;
    }

    public static void getBitmapsFromVideo(String name , int level){


        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
        }
        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(storagePath+"/"+name);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = Integer.parseInt(time) / 1000;
        Log.e("videoSecond",seconds+"!!");
        for(int i = 0 ; i < seconds ; i++){
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000 , MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            saveBitmap(bitmap,storagePath + "/image" + i + ".jpg");
        }*/

    }

    public static void saveBitmap(Bitmap bitmap , String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            Log.e("saveBitmap","complete---");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
