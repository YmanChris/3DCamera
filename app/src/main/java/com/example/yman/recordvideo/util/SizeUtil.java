package com.example.yman.recordvideo.util;

import android.hardware.Camera;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xiaoqingsong on 2017/1/18.
 */
public class SizeUtil {

    private  CameraSizeComparator sizeComparator = new CameraSizeComparator();

    private static SizeUtil sizeutil = null;

    private SizeUtil(){

    }

    public static SizeUtil getInstance(){
        if(sizeutil == null){
            sizeutil = new SizeUtil();
            return sizeutil;
        }
        else{
            return sizeutil;
        }
    }

    /**
     * 获取预览尺寸
     */
    public  Camera.Size getPropPreviewSize(List<Camera.Size> list, float bestRatio){

        //先降序
        Collections.sort(list, sizeComparator);

        int i =0;
        for(Camera.Size s:list){
            //找到了
            if(equalRate(s, bestRatio)){
                Log.i("SizeUtil", "PreviewSize:w = " + s.width + " h = " + s.height);
                break;
            }
            i++;
        }

        //若没找到
        if(i == list.size()){
            return getProperSize(list,bestRatio);
        }
        return list.get(i);
    }


    /**
     * 谷歌的官方办法
     */
    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.02;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // 找合适的
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // 若找不到
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    public static Camera.Size getPropPictureSize(List<Camera.Size> list, int minHeight){

        int i = 0;
        for(Camera.Size s:list)
        {
            if(s.height>minHeight)
            {
                Log.e("util", "PictureSize = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }


    /**
     * 判断比例是否相等
     */
    public static boolean equalRate(Camera.Size s, float rate){

        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.02)
        {
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * 获取最合适的
     */
    public static Camera.Size getProperSize(List<Camera.Size> list, float bestRatio){
        if(list!=null){
            float temp=10;
            int tempIndex=0;
            for(int i=0;i<list.size();i++){
                Camera.Size s=list.get(i);
                float r = (float)(s.width)/(float)(s.height);
                float diff=Math.abs(r-bestRatio);
                if(diff<temp){
                    temp=diff;
                    tempIndex=i;
                }
            }
            return list.get(tempIndex);
        }
        return null;
    }

    /**
     * 降序比较器
     */
    public  class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width < rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }
}

