package com.example.yman.recordvideo.model;

/**
 * Created by fengyijun on 2016/12/19.
 */

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.yman.recordvideo.util.CamParaUtil;
import com.example.yman.recordvideo.util.SizeUtil;

import java.io.IOException;
import java.util.List;


public class CameraInterface {

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private int mCameraId = -1;
    private static CameraInterface mCameraInterface;

    public interface CamOpenOverCallback {
        void cameraHasOpened();
    }

    public static synchronized CameraInterface getInstance() {

        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public void doOpenCamera(CamOpenOverCallback callback, int cameraId) {
        mCamera = Camera.open(cameraId);
        mCameraId = cameraId;
        mCamera.setPreviewCallbackWithBuffer(null);
        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    public void doStartPreview(SurfaceHolder holder, int w,int h) {

        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            List<Size> sizes=mParams.getSupportedPreviewSizes();
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);

//            Size proSize=SizeUtil.getInstance().getPropPreviewSize(sizes,previewRate);

            Size proSize= SizeUtil.getInstance().getOptimalPreviewSize(sizes,w,h);

            Log.w("properSize:", proSize.width + "  " + proSize.height);

            mParams.setPreviewSize(proSize.width, proSize.height);
//            mParams.setPreviewSize(1280,720);



            mParams.setRecordingHint(true);
            mCamera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreviewing = true;

            mParams = mCamera.getParameters();
        }
    }

    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera.Parameters getCameraParams() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            return mParams;
        }
        return null;
    }

    public Camera getCameraDevice() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }
}
