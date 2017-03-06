package com.example.yman.recordvideo.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.model.Goods;
import com.example.yman.recordvideo.util.FileUtils;
import com.example.yman.recordvideo.util.StringUtils;

import java.io.File;


/**
 * Created by yinxiangyang on 2017/1/24.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private TextView startRecord,see;
    private EditText skuEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
    }
    public void initView(){
        startRecord = (TextView) findViewById(R.id.start_record);
        see = (TextView) findViewById(R.id.see);
        skuEdit = (EditText) findViewById(R.id.p_sku);
        startRecord.setOnClickListener(this);
        see.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.see:
                /*String cmd = "-y -i /sdcard/1234.mpg /sdcard/image%d.jpg";
                String[] command = cmd.split(" ");
                if (command.length != 0) {
                    execFFmpegBinary(command);
                } else {
                    Toast.makeText(MainActivity.this, "You cannot execute empty command", Toast.LENGTH_LONG).show();
                }*/
                intent = new Intent(this,VideoListsActivity.class);
                startActivity(intent);
               // searchNativeVideo();
                break;
            case R.id.start_record:
                //RecordUtils.startRecordVideo(new Goods(5));
                if(StringUtils.isEmpty(skuEdit.getText().toString())){
                    Toast.makeText(this,"sku值不能为空",Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    File videoFile = new File(FileUtils.getStoragePath(),
                            skuEdit.getText().toString() + ".mpg");
                    if(videoFile.exists())
                        Toast.makeText(this,"该sku已存在，请重新输入",Toast.LENGTH_SHORT).show();
                    else {
                        Goods goods = new Goods(skuEdit.getText().toString());
                        intent = new Intent(MainActivity.this, VideoRecorderActivity.class);
                        intent.putExtra("sku", goods.getSku());
                        startActivity(intent);
                        skuEdit.setText("");
                    }
                }
                break;
            default:
                break;
        }
    }
    public void searchNativeVideo(){
        if (this != null) {
            Cursor cursor = this.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
         //   Log.e(TAG,"this != null");
            if (cursor != null) {
               // Log.e(TAG,"cursor != null");
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    Log.e("id",id+"");
                    Log.e("title",title);
                    Log.e("album",album);
                    Log.e("artist",artist);
                    Log.e("displayName",displayName);
                    Log.e("mimeType",mimeType);
                    Log.e("path",path);
                    Log.e("duration",duration+"");
                    Log.e("size",size+"");
                    Log.e("------","--------");
                 //   names.add(title);
                //    paths.add(path);
                }
             //   Log.e(TAG,"cursor close");
                cursor.close();
            }
        }
        else {
            Toast.makeText(this,"this 为null",Toast.LENGTH_SHORT).show();
        }
    }


}
