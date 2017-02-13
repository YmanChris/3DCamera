package com.example.yman.recordvideo.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.model.VideoInfo;

import java.io.FileDescriptor;
import java.util.List;

/**
 * Created by Think on 2017/2/1.
 */

public class VideoAdapter extends BaseAdapter{
    private Context context;
    private List<VideoInfo> videoInfos;
    public VideoAdapter(Context context, List<VideoInfo> videoInfos){
        this.context = context;
        this.videoInfos = videoInfos;
    }
    @Override
    public int getCount() {
        if(videoInfos == null)
            return 0;
        else
            return videoInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return videoInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view ;
        ViewHolder holder = null;
        if(convertView == null){
            view = View.inflate(context,R.layout.video_item,null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.video_image);
            holder.textView = (TextView) view.findViewById(R.id.video_name);
            view.setTag(holder);
        }
        else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        String name = videoInfos.get(position).getName();
        holder.textView.setText(name);
        if(videoInfos.get(position).getBitmap() != null) {
            holder.image.setImageBitmap(videoInfos.get(position).getBitmap());
        }


        return view;
    }
    class ViewHolder{
        private ImageView image;
        private TextView textView;
    }

}
