package com.example.yman.recordvideo.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.yman.recordvideo.R;

import java.io.File;

/**
 * Created by yinxiangyang on 2017/2/12.
 */

public class PicturePlayActivity extends BaseActivity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private ImageView imageView;
    private float x = 0;
    int memClass;
    private LruCache<String,Bitmap> mMemoryCache;
    private int position = 1;
    private int total = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_play);
        imageView = (ImageView) findViewById(R.id.play_view);
        init();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor != null){
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap;
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(position == total)
                        break;
                    position++;
                    bitmap = mMemoryCache.get("image"+position+".jpg");
                    imageView.setImageBitmap(bitmap);
                    break;
                case 2:
                    if(position == 1)
                        break;
                    position--;
                    bitmap = mMemoryCache.get("image"+position+".jpg");
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };

    /* 对于陀螺仪，测量的是x、y、z三个轴向的角速度，分别从values[0]、values[1]、values[2]中读取，单位为弧度/秒。*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(x == 0) {
            x = sensorEvent.values[0];
            return;
        }

        if(x >sensorEvent.values[0]){
            handler.sendEmptyMessage(1);
        }
        else if(x < sensorEvent.values[0]){
            handler.sendEmptyMessage(2);
        }
        x = sensorEvent.values[0];
        Log.e("PicturePlayActivity","x:"+sensorEvent.values[0]+"y:"+sensorEvent.values[1]+"z:"+sensorEvent.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this);
        mMemoryCache.evictAll();
        super.onDestroy();
    }

    public void init(){
        memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        mMemoryCache = new LruCache<String,Bitmap>(memClass * 1012 * 1024 / 8){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        addBitmapToMemory();
    }

    public void addBitmapToMemory(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        File file = new File(Environment.getExternalStorageDirectory()+"/com.jd.record");
        File[] files = null;
        if(file.isDirectory()){
            files = file.listFiles();
        }
        for(File f : files){
            if(f.getName().endsWith(".jpg")){
                Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(),options);
                mMemoryCache.put(f.getName(),bitmap);
                Log.e("memory",f.getName());
                total ++;
            }
        }
        position = total / 2;
        Bitmap bitmap = mMemoryCache.get("image"+position+".jpg");
        imageView.setImageBitmap(bitmap);
    }
}
