package com.example.yman.recordvideo.widget.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yman.recordvideo.R;
import com.example.yman.recordvideo.model.Constants;
import com.example.yman.recordvideo.util.StringUtils;

/**
 * Created by Think on 2017/1/31.
 */

public class CommonDialog extends DialogFragment{
    public String type = "";
    public String msg = "";
    private View rootView;
    public static CommonDialog newInstance(String type,String msg){
        CommonDialog dialog = new CommonDialog();
        dialog.type = type;
        dialog.msg = msg;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(inflater,container);
        return rootView;
    }
    public void initView(LayoutInflater inflater,ViewGroup container){
        if(StringUtils.isEquals(type, Constants.TOASTDIALOG)){
            initToastDialog(inflater,container);
        }
    }

    /**
     * ToastDialog初始化
     * @param inflater
     * @param container
     */
    public void initToastDialog(LayoutInflater inflater,ViewGroup container){
        rootView = inflater.inflate(R.layout.dialog_toast,container,false);
        TextView textView = (TextView) rootView.findViewById(R.id.toast_msg);
        textView.setText(msg);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            // 解决java.lang.IllegalStateException: Can not perform this action问题
            final FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            try {
                ft.commitAllowingStateLoss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
