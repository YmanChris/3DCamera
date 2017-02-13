package com.example.yman.recordvideo.util;

/**
 * Created by fengyijun on 2016/12/19.
 */
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CamParaUtil {
    private static final String TAG = "ejoy";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;
    private CamParaUtil(){

    }
    public static CamParaUtil getInstance(){
        if(myCamPara == null){
            myCamPara = new CamParaUtil();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }

    /**
     * 查找最合适的尺寸
     * @param list
     * @param bestRatio
     * @param videoSize
     * @return
     */
    public  Size getPropPreviewSize(List<Camera.Size> list, float bestRatio, Size videoSize) {
        // 从小到大排序
        Collections.sort(list, sizeComparator);

        Size bestSize = videoSize;

        for(Size s : list) {
            float ratio = ratio(bestSize);

            // 找到最合适的
            if (bestRatio == ratio) {
                break;
            }

            float curRatio = ratio(s);
            float diff = Math.abs(curRatio - bestRatio);
            // 找比例最接近的
            if (diff < Math.abs(curRatio - ratio)) {
                bestSize = s;
            }
        }

        return bestSize;
    }

    /**
     * 高宽比
     */
    private float ratio(Size size) {
        return (float)size.height / (float)size.width;
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size>{
        public int compare(Size lhs, Size rhs) {
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }

    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
        }

    }

    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
