package com.example.yman.recordvideo.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.model.Constants;
import com.example.yman.recordvideo.util.FileUtils;
import com.example.yman.recordvideo.widget.CameraSurfaceView;
import com.example.yman.recordvideo.widget.circleProgressBar.CircleProgressButton;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yinxiangyang on 2017/1/24.
 */

public class VideoRecorderActivity extends BaseActivity implements SurfaceHolder.Callback,View.OnClickListener,Camera.PreviewCallback{
    String TAG = VideoRecorderActivity.class.getSimpleName();

    private CircleProgressButton circleProgressButton;
    private CameraSurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private RadioButton light,exchangeAngel;
    private MediaRecorder mediarecorder;

    private String sku;
    private boolean isRecording = false;

    private boolean isPreview = false;

    private float screenProp;
    private int width;
    private int height;

    private Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera.setOneShotPreviewCallback(this);
        setContentView(R.layout.activity_video_recorder);
        sku = getIntent().getStringExtra("sku");
        //设置半透明模式
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initView();
    }
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            stopRecordVideo();
        }
    };
    public void initView() {
        circleProgressButton = (CircleProgressButton) findViewById(R.id.circleProgressButton);
        circleProgressButton.setText("");

        surfaceView = (CameraSurfaceView) findViewById(R.id.CaptureView);
        surfaceView.setOnClickListener(this);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera != null){
            holder.removeCallback(this);
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.lock();
            camera.release();
            camera = null;
        }
        surfaceView = null;
        surfaceHolder = null;
        mediarecorder = null;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(VideoRecorderActivity.this,"you click the button",Toast.LENGTH_SHORT).show();
        if(!isRecording) {
            startRecordVideo();
        }
        else{
           // stopRecordVideo();
        }
    }

    public void stopRecordVideo(){
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            releaseMediaRecorder();
            isRecording = false;
            camera.lock();

        }
    }
    public void startRecordVideo(){
        configureMediaRecorder();
        prepareConfigureMediaRecorder();
        mediarecorder = new MediaRecorder();
        mediarecorder.reset();
        if(camera == null)
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);    // 从照相机采集视频
        camera.setDisplayOrientation(90);   //解决画面角度旋转90度情况
        Camera.Parameters mParams = camera.getParameters();
        List<Camera.Size> sizeList = mParams.getSupportedPreviewSizes();
        List<Camera.Size> previewSizes = mParams.getSupportedPreviewSizes();
        Iterator<Camera.Size> itor = sizeList.iterator();
        Iterator<Camera.Size> previewItor = previewSizes.iterator();
        while (previewItor.hasNext()) {
            Camera.Size cur = previewItor.next();
            Log.i(TAG, "PreviewSize    width = " + cur.width + " height = " + cur.height);
        }
        float disparity = 1000;
        while (itor.hasNext()) {
            Camera.Size cur = itor.next();
            float prop = (float) cur.height / (float) cur.width;
            if (Math.abs(screenProp - prop) < disparity) {
                disparity = Math.abs(screenProp - prop);
                width = cur.width;
                height = cur.height;
            }
            Log.i(TAG, "width = " + cur.width + " height = " + cur.height);

        }
        Log.i(TAG, "Mmkesure width = " + width + " height = " + height);
        mParams.setPreviewSize(width, height);
        mParams.setPictureSize(width, height);
        mParams.setRecordingHint(true);
        mParams.set("orientation", "portrait");
        camera.setDisplayOrientation(90);
        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mParams.setRecordingHint(true);
        camera.setParameters(mParams);
        camera.unlock();
        mediarecorder.setCamera(camera);
        mediarecorder.setOrientationHint(90);   //视频旋转90度
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediarecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        File videoFile = new File(FileUtils.getStoragePath(),
                sku + ".mpg");
        if(videoFile.exists()){
            Toast.makeText(this,"该sku已存在，请重新输入",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mediarecorder.setPreviewDisplay(surfaceView.getHolder()
                .getSurface());
        mediarecorder.setOutputFile(videoFile.getAbsolutePath());
        try {
            mediarecorder.prepare();                                        // 预期准备
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.createNewFile(sku);
        mediarecorder.start();
        isRecording = true;
        timer.schedule(task, Constants.RECORDTIME * 1000);
    }
    public void startTimeVideoRecord(){

    }
    public void configureMediaRecorder(){

    }
    public void prepareConfigureMediaRecorder(){

    }
    public void releaseMediaRecorder(){
        if(mediarecorder != null) {
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;
        }
    }

    @Override
    protected void onDestroy() {
        releaseMediaRecorder();
        camera = null;
        super.onDestroy();
    }
    public Point getScreenMetrics(Context context){
        DisplayMetrics dm =context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
        return new Point(w_screen, h_screen);

    }
    private Point getBestCameraResolution(Camera.Parameters parameters, Point screenResolution){
        float tmp = 0f;
        float mindiff = 100f;
        float x_d_y = (float)screenResolution.x / (float)screenResolution.y;
        Camera.Size best = null;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for(Camera.Size s : supportedPreviewSizes){
            tmp = Math.abs(((float)s.height/(float)s.width)-x_d_y);
            if(tmp<mindiff){
                mindiff = tmp;
                best = s;
            }
        }
        return new Point(best.width, best.height);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.e("abc","abc");
    }
    /**
     * 初始化摄像头各参数
     * */
    private void initCamera(){
        if(!isPreview) {
            /*if (camera != null) {
                camera.release();
            }*/
            if(camera == null)
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if(camera!=null && !isPreview){
            try {
                Camera.Parameters parameters = camera.getParameters();
                Point p = getBestCameraResolution(parameters,getScreenMetrics(this));
                //每台手机的摄像头所支持的图像预览或拍摄尺寸不尽相同，
                //如果设置的图像尺寸，摄像头不支持，则会出错，
                //因此在真机上测试前，先要确定摄像头支持哪些尺寸
                parameters.setPreviewSize(p.x, p.y);  //设置预览图像的尺寸大小
                parameters.setPreviewFpsRange(20, 30);                 //设置每秒显示10-20帧
                // 横竖屏镜头自动调整
                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                    parameters.set("orientation", "portrait");
                    parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
                    camera.setDisplayOrientation(90); // 在2.2以上可以使用
                } else{
                    // 如果是横屏
                    parameters.set("orientation", "landscape");
                    camera.setDisplayOrientation(0); // 在2.2以上可以使用
                }
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);                     //通过SurfaceView显示取景画面
                //回调处理预览视频流类中的onPreviewFrame方法
                //在onPreviewFrame方法中，启动发送视频流的线程
                camera.setPreviewCallback(this);
                camera.startPreview();           //开始预览
                camera.autoFocus(null);         //自动对焦
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }
}
