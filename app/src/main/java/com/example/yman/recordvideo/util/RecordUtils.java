package com.example.yman.recordvideo.util;

import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.example.yman.recordvideo.YYApplication;
import com.example.yman.recordvideo.model.Goods;

import java.io.IOException;

/**
 * Created by Think on 2017/1/24.
 */

public class RecordUtils {
    static int timeCount = 10;//时间计数器默认为10
    static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    static String fileName = "";
    static MediaRecorder recorder;


    /**
     * 开始录制视频
     * @param goods
     */
    public static void startRecordVideo(Goods goods){
        fileName = filePath + String.valueOf(goods.getSku()) + ".mp4";
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置采集声音
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//设置采集图像
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置视频音频输出格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码格式
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);//设置视频编码格式
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Toast.makeText(YYApplication.getInstance(),"录制出错",Toast.LENGTH_SHORT).show();
            }
        });
        recorder.setOutputFile(fileName);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    /**
     * 结束录制视频
     */
    public static void stopRecordVideo(){
        recorder.stop();
        recorder.release();
    }


}
