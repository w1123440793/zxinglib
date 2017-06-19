package com.google.zxing.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.zxing.AmbientLightManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.R;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.callback.ResultPointCallback;
import com.google.zxing.callback.ScanQRCallBack;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.handler.CaptureHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Description 二维码扫描view
 */
public class ScanQRcodeView extends FrameLayout implements SurfaceHolder.Callback
        ,ScanQRCallBack {
    private static final String TAG = ScanQRcodeView.class.getSimpleName();
    private boolean hasSurface = false;
    private CameraManager cameraManager;
    private SurfaceView cameraView;
    private ScanPreView preView;
    private CaptureHandler handler;
    private AmbientLightManager ambientLightManager;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;

    @Override
    public CaptureHandler getHandler() {
        return handler;
    }

    @Override
    public ResultPointCallback getResultPointCallback() {
        return new ResultPointCallback() {
            @Override
            public void foundPossibleResultPoint(ResultPoint point) {

            }
        };
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ScanQRcodeView(Context context) {
        super(context);
        initView(context);
    }

    public ScanQRcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScanQRcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void setResultSuc(Result result, Bitmap bitmap) {
        if (onScanListener != null)
            onScanListener.onScanSuc(result.getText());
    }

    private void initView(Context context) {
        handler = null;
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        LayoutInflater.from(context).inflate(R.layout.layout_camera, this, true);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        preView = (ScanPreView) findViewById(R.id.pre_view);
        hasSurface = false;
        ambientLightManager = new AmbientLightManager(context);
    }

    @Override
    protected void onAttachedToWindow() {
        Log.e(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
        final SurfaceHolder surfaceHolder = cameraView.getHolder();
        if (hasSurface) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    initCamera(surfaceHolder);
                }
            },300);
        } else {
            surfaceHolder.addCallback(ScanQRcodeView.this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.e(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
        ambientLightManager.stop();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = cameraView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    initCamera(holder);
                }
            },300);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged");
//        cameraManager.setManualFramingRect(width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        hasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager == null) {
            cameraManager = new CameraManager(getContext().getApplicationContext());
            ambientLightManager.start(cameraManager);
            preView.setCameraManager(cameraManager);
            preView.startPre();
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }


        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
//            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException e) {
            Log.w(TAG, e);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getResources().getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (onScanListener != null)
                    onScanListener.onScanFail(getResources().getString(R.string.msg_camera_framework_bug));
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, String string) {

    }

    private OnScanListener onScanListener;

    public void setOnScanListener(OnScanListener listener) {
        this.onScanListener = listener;
    }

    public interface OnScanListener {
        void onScanSuc(String result);

        void onScanFail(String fail);
    }
}
