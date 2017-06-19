package com.google.zxing.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.zxing.R;
import com.google.zxing.camera.CameraManager;

/**
 * Author  wangchenchen
 * CreateDate 2017-6-7.
 * Email wcc@jusfoun.com
 * Description
 */
public class ScanPreView extends View {

    private static final long ANIMATION_DELAY = 80L;
    private static final int POINT_SIZE = 6;
    private Handler handler = new Handler();
    private boolean hasSurface = false;
    private CameraManager cameraManager;
    private final Paint paint;
    private final int maskColor;
    private final int scanLineColor;
    private final int lineHeight;
    private final Rect line;
    private int count;
    private ValueAnimator animator;
    private Bitmap bitmap;
    private final TextPaint textPaint;

    public ScanPreView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.scan);
        maskColor = resources.getColor(R.color.viewfinder_mask);
        scanLineColor = resources.getColor(R.color.scan_line_color);
        lineHeight =  resources.getDimensionPixelSize(R.dimen.height_scan_qr_code_line);
        textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(scanLineColor);
        textPaint.setTextSize(resources.getDimensionPixelSize(R.dimen.size_scan_qr_code_text));
        line = new Rect();

        animator=new ValueAnimator();
        animator.setDuration(3000);
        animator.setRepeatMode(ValueAnimator.INFINITE);
        animator.setRepeatCount(-1);
        animator.setFloatValues(0,1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (cameraManager == null) {
                    return;
                }
                Rect frame = cameraManager.getFramingRect();
                if (frame==null)
                    return;
                float value=Float.parseFloat(animation.getAnimatedValue().toString());
                int index= (int) (value*frame.height());
                count=index;
                if (frame.top+count>frame.bottom){
                    count=0;
                }
                postInvalidateDelayed(ANIMATION_DELAY,
                        frame.left - POINT_SIZE,
                        frame.top - POINT_SIZE,
                        frame.right + POINT_SIZE,
                        frame.bottom + POINT_SIZE);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (bitmap!=null){
            canvas.drawBitmap(bitmap,null,frame,paint);
        }
        paint.setColor(scanLineColor);
        if (frame.top+count>frame.bottom) {
            count = 0;
        }else {
            count+=2;
        }
        line.set(frame.left,frame.top+count,frame.right,frame.top+count+lineHeight);

        canvas.drawRect(line,paint);

        canvas.drawText("请将设备二维码/条形码放入框内,即可自动扫描",frame.left,frame.bottom+50,textPaint);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator.isRunning())
            animator.cancel();
    }

    public void startPre(){
        if (animator!=null)
            animator.start();
    }

    public void setCameraManager(CameraManager cameraManager){
        this.cameraManager=cameraManager;
    }

}
