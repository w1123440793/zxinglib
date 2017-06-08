package com.google.zxing.callback;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.handler.CaptureHandler;

/**
 * Author  wangchenchen
 * CreateDate 2017-6-7.
 * Email wcc@jusfoun.com
 * Description
 */
public interface ScanQRCallBack {
    public Context getContext();
    public CameraManager getCameraManager();
    public CaptureHandler getHandler();
    public ResultPointCallback getResultPointCallback();

    void setResultSuc(Result result, Bitmap bitmap);


}
