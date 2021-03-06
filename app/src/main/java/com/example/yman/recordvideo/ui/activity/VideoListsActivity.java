package com.example.yman.recordvideo.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.model.SpeedConstants;
import com.example.yman.recordvideo.model.VideoInfo;
import com.example.yman.recordvideo.ui.adapter.VideoAdapter;
import com.example.yman.recordvideo.util.FileUtils;
import com.example.yman.recordvideo.util.LogUtils;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Created by yinxiangyang on 2017/2/1.
 */

public class VideoListsActivity extends BaseActivity{

    private String TAG = "VideoListsActivity";

    @Inject
    FFmpeg ffmpeg;

    private TextView tips;
    private GridView listView;
    private VideoAdapter adapter;
    private List<VideoInfo> videoInfos = new ArrayList<>();

    private int clickPosition = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lists);

        ButterKnife.inject(this);
        ObjectGraph.create(new DaggerDependencyModule(this)).inject(this);
        loadFFMpegBinary();

        init();
        initView();


    }
    public void init(){
        videoInfos = FileUtils.getVideoList();
    }

    public void initView(){
        tips = (TextView) findViewById(R.id.no_video_tips);
        if (videoInfos == null || videoInfos.size() == 0){
            tips.setText(getString(R.string.no_video_tips));
        }
        else
            tips.setVisibility(View.GONE);
        listView = (GridView) findViewById(R.id.list);
        adapter = new VideoAdapter(this,videoInfos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean isExists = FileUtils.checkImageExists(videoInfos.get(i).getName());
                clickPosition = i;
                if(!isExists){
                    String cmd = "-y -i "+ Environment.getExternalStorageDirectory()+"/com.jd.record/"+videoInfos.get(i).getName()+".mpg"+" -r 30 -q:v 2 "+Environment.getExternalStorageDirectory()+"/com.jd.record/"+videoInfos.get(i).getName()+"/image%d.jpg";
                    String[] command = cmd.split(" ");
                    if (command.length != 0) {
                        execFFmpegBinary(command);
                    } else {
                        Toast.makeText(VideoListsActivity.this, "You cannot execute empty command", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Intent intent = new Intent(VideoListsActivity.this, PicturePlayActivity.class);
                    intent.putExtra("sku", videoInfos.get(i).getName());
                    startActivity(intent);
                }
            }
        });
    }
    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }
    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(VideoListsActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Device not supported")
                .setMessage("FFmpeg is not supported on your device")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VideoListsActivity.this.finish();
                    }
                })
                .create()
                .show();

    }
    public void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Toast.makeText(VideoListsActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String s) {
                    Toast.makeText(VideoListsActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VideoListsActivity.this, PicturePlayActivity.class);
                    intent.putExtra("sku", videoInfos.get(clickPosition).getName());
                    startActivity(intent);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg "+command + "----------------"+s);
                    Toast.makeText(VideoListsActivity.this,"onProgress",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    Toast.makeText(VideoListsActivity.this,"onStart",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
                    Toast.makeText(VideoListsActivity.this,"onFinish",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }
}
