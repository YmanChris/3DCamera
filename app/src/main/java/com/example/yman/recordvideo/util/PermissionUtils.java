package com.example.yman.recordvideo.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by denny on 2017/1/16.
 */

public class PermissionUtils {

    public final static int REQ_CODE_PERM_CAMERA = 1;

    /**
     * 是否有相机权限
     * @param activity
     * @return
     */
    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请相机权限
     * @param activity
     */
    public static void reqCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.CAMERA }, REQ_CODE_PERM_CAMERA);
    }
}
