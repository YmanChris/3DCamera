package com.example.yman.recordvideo.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.util.LogUtils;
import com.example.yman.recordvideo.widget.CameraSurfaceView;

import java.io.File;
import java.util.Vector;

/**
 * Created by yinxiangyang on 2017/2/12.
 */

public class PicturePlayActivity extends BaseActivity implements SensorEventListener,SurfaceHolder.Callback{

    private String TAG = "PicturePlayActivity";

    private SensorManager sensorManager;
    private Sensor sensor;
    private float x = 0;//当前x坐标
    int memClass;
    private String sku;
    private LruCache<String,Bitmap> mMemoryCache;
    private int position = 1;//当前位置
    private int total = 0;//缓存图片总数量
    private int level = 30;//采样帧率
    int picNums = 0;//图片总数量
    int picPerSecond = 0;//视频帧率
    int seconds = 10;//视频时间
    int CacheSize = 100;//缓存大小
    int[] CacheIndex;//需展示所有图片index
    private int index = 0;
    private Vector<Integer> vector;
    private int vertorSize = 50;

    private ProgressBar imageView;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Boolean isPlay = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_play);
        sku = getIntent().getStringExtra("sku");
        initView();
        init();


    }


    /* 对于陀螺仪，测量的是x、y、z三个轴向的角速度，分别从values[0]、values[1]、values[2]中读取，单位为弧度/秒。*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
      //  Log.e("PicturePlayActivity","x:"+sensorEvent.values[0]+"y:"+sensorEvent.values[1]+"z:"+sensorEvent.values[2]);
        Bitmap bitmap;
        if(x == 0) {
            x = sensorEvent.values[2];
            synchronized (vector) {
                if(vector.size() <= vertorSize)
                    vector.add(position);
            }
            return;
        }
        if(Math.abs(x - sensorEvent.values[2]) < 0.1 * (10)){
            return;
        }
        if(x >sensorEvent.values[2]){

            if(position >= total)
                position = total;
            else {
                int n = (int)(Math.abs(x - sensorEvent.values[2]));
                for(int i = 0 ; i < n ; i++){
                    if(position >= total)
                        break;
                    else {
                        position++;
                     //   updateCache();
                        synchronized (vector) {
                            if(vector.size() <= vertorSize)
                                vector.add(position);
                        }
                    }
                }
            }

        }
        else if(x < sensorEvent.values[2]){
            if(position <= 1)
                position = 1;
            else {
                int n = (int)(Math.abs(x - sensorEvent.values[2]));
                for(int i = 0 ; i < n ; i++){
                    if(position <= 1)
                        break;
                    else {
                        position--;
                    //    updateCache();
                        synchronized (vector) {
                            if(vector.size() <= vertorSize)
                                vector.add(position);
                        }
                    }
                }
            }
        }
        x = sensorEvent.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this);
        mMemoryCache.evictAll();
        synchronized (isPlay) {
            isPlay = false;
        }
        synchronized (vector) {
            vector.clear();
        }
        super.onDestroy();
    }

    public void init(){
        vector = new Vector<>();
        memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        mMemoryCache = new LruCache<String,Bitmap>(memClass * 1012 * 1024 / 8){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        addBitmapToMemory();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor != null){
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void initView(){
        imageView = (ProgressBar) findViewById(R.id.play_view);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }


    public void addBitmapToMemory(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        File file = new File(Environment.getExternalStorageDirectory()+"/com.jd.record/"+sku);
        File[] files = null;
        if(file.isDirectory()){
            files = file.listFiles();
        }
        picNums = files.length - 1;//图片总数量
        picPerSecond = picNums / seconds;//视频帧率
        total = seconds * level;//缓存数量
        position = 0;//当前位置
        index = 0;
        CacheIndex = new int[total];//缓存数组，用于记录需要缓存图片位置


        for(File f : files){
            if(f.getName().endsWith(".jpg")){
                position ++;
                if(position % (picPerSecond / level) == 0 && index <  total) {

                    CacheIndex[index] = position;
                    index++;

                }
            }
        }
        position = total / 2;
        index = position - CacheSize / 2;

        for(int i = index; i < index + CacheSize ; i++){
            Bitmap bitmap = BitmapFactory.decodeFile(files[CacheIndex[i]].getPath(), options);
            mMemoryCache.put(CacheIndex[i]+"", bitmap);
        }

    }



    public void updateCache(){
        if(position >= total - (CacheSize / 2) || (position <= CacheSize / 2)){
            return;
        }
        else {
            if(position <= (total - CacheSize) / 2 || position >= total - CacheSize / 2) {
                return;
            }

            File file = new File(Environment.getExternalStorageDirectory()+"/com.jd.record/"+sku);
            File[] files = null;
            if(file.isDirectory()){
                files = file.listFiles();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bitmap = BitmapFactory.decodeFile(files[0].getPath(), options);

        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
                Paint paint = new Paint();
                while(isPlay){
                    if(vector!= null && vector.size() > 0){
                        try {
                            canvas = surfaceHolder.lockCanvas(null);
                            onDraw(canvas,paint);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                        catch (Exception e){}

                    }
                }
            }
        }).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    public void onDraw(Canvas canvas, Paint paint){
        if(vector!= null && vector.size() > 0){
            Bitmap bitmap;
            synchronized (vector) {
                bitmap = mMemoryCache.get(vector.get(0) + "");
                int n = vector.size() / 5;
                for(int i = 0 ; i < n ; i++){
                    vector.remove(0);
                }
                vector.remove(0);
            }
            DisplayMetrics dm = getResources().getDisplayMetrics();
            canvas.drawBitmap(bitmap,0,0,paint);

        }
    }
}
