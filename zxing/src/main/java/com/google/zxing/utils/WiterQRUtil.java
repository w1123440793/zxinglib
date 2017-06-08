package com.google.zxing.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.Version;

import java.util.EnumMap;
import java.util.Map;

/**
 * Author  wangchenchen
 * CreateDate 2017-6-8.
 * Email wcc@jusfoun.com
 * Description
 */
public class WiterQRUtil {

    /**
     *普通二维码
     * @param content 二维码内容
     * @param size 二维码大小
     * @param qrColor 二维码颜色
     * @param bgColor 二维码背景颜色
     * @return
     */
    public static Bitmap witerQR(String content,int size,int qrColor,int bgColor){
        Map<EncodeHintType,Object> hints=new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix result;
        try {
            result= new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,size,size,hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        int width=result.getWidth();
        int height=result.getHeight();
        int[] pixels=new int[width*height];

        for (int y = 0; y < height; y++) {
            int offset=y*width;
            for (int x = 0; x < width; x++) {
                //给二维码设置颜色
                pixels[offset+x]=result.get(x,y)?qrColor:bgColor;
            }
        }

        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        return bitmap;
    }

    /***
     * 背景为Logo
     * @param content 二维码内容
     * @param logo 背景图
     * @param size 大小
     * @param qrColor 二维码颜色
     * @return
     */
    public static Bitmap witerQRBgByLogo(String content,Bitmap logo,int size,int qrColor){
        Map<EncodeHintType,Object> hints=new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix result;
        try {
            result= new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,size,size,hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        logo=Bitmap.createScaledBitmap(logo,size,size,false);
        int width=result.getWidth();
        int height=result.getHeight();
        int[] pixels=new int[width*height];

        for (int y = 0; y < height; y++) {
            int offset=y*width;
            for (int x = 0; x < width; x++) {
                pixels[offset+x]=result.get(x,y)?qrColor:(logo.getPixel(x,y)&0x50ffffff);
            }
        }

        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        return bitmap;
    }

    /**
     * logo在中间二维码
     * @param content 二维码内容
     * @param logo 背景图
     * @param size 大小
     * @param qrColor 二维码颜色
     * @param bgColor 二维码背景颜色
     * @return
     */
    public static Bitmap witerQRCenterLogo(String content,Bitmap logo,int size,int qrColor,int bgColor){
        Map<EncodeHintType,Object> hints=new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix result;
        try {
            QRCodeWriter writer=new QRCodeWriter();
            result= writer.encode(content, BarcodeFormat.QR_CODE,size,size,hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        logo=Bitmap.createScaledBitmap(logo,size,size,false);
        int width=result.getWidth();
        int height=result.getHeight();
        Matrix m=new Matrix();
        m.setScale(0.2f,0.2f);//设置loge大小为二维码1/5
        logo=Bitmap.createBitmap(logo,0,0,logo.getWidth(),logo.getHeight(),m,false);
        int[] pixels=new int[width*height];

        for (int y = 0; y < height; y++) {
            int offset=y*width;
            for (int x = 0; x < width; x++) {
                if (x>width/2-width/10
                        &&x<width/2+width/10
                        &&y>height/2-height/10
                        &&y<height/2+height/10){
                    pixels[offset+x]=logo.getPixel(x-width/2+width/10
                    ,y-height/2+height/10);
                }else {
                        pixels[offset+x]=result.get(x,y)?qrColor:bgColor;
                }
            }
        }

        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        return bitmap;
    }

    /**
     * 修改二维码三个角颜色
     * @param content 二维码内容
     * @param topLeftColor 左上角颜色
     * @param topRightColor 右上角颜色
     * @param bottomLeftColor 左下角颜色
     * @param bgColor 背景颜色
     * @param QRColor 二维码颜色
     * @param size 二维码大小
     * @return
     */
    public static Bitmap writerQREditPositionColor(String content,int topLeftColor,int topRightColor
    ,int bottomLeftColor,int bgColor,int QRColor,int size){
        int start=0;
        int end=7;
        Map<EncodeHintType,Object> hints=new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix result;
        Version version;
        try {
            QRCodeWriter writer=new QRCodeWriter();
            result= writer.encode(content, BarcodeFormat.QR_CODE,size,size,hints);
            version=writer.getVersion();
            if (version==null)
                return null;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        //TODO:算法有问题
        int totalModelNum=(version.getVersionNumber()-1)*4+5+16;//获取单边模块数
        int bit[]=result.getTopLeftOnBit();
        int width=result.getWidth();
        int height=result.getHeight();

        int modelWidth=width/totalModelNum;
        int topEndX=bit[0]+modelWidth*end;
        int topStartX=bit[0]+modelWidth*start;
        int topStartY=bit[0]+modelWidth*start;
        int topEndY=bit[0]+modelWidth*end;

        int rightStartX=(totalModelNum-end)*modelWidth+bit[0];
        int rightEndX=width-modelWidth*end-bit[0];
        int leftStartY=height-modelWidth*end-bit[1];
        int leftEndY=height-modelWidth*start-bit[1];

        int[] pixels=new int[width*height];

        for (int y = 0; y < height; y++) {
            int offset=y*width;
            for (int x = 0; x < width; x++) {
                if(x>=topStartX&&x<topEndX&&y>=topStartY&&y<topEndY){
                    //左上角颜色
                    pixels[offset + x] = result.get(x, y) ? topLeftColor: bgColor;
                }else if(x<rightEndX&&x>=rightStartX&&y>=topStartY&&y<topEndY){
                    //右上角颜色

                    pixels[offset + x] = result.get(x, y) ? topRightColor:  bgColor;
                }else if(x>=topStartX&&x<topEndX&&y>=leftStartY&&y<leftEndY){
                    //左下角颜色
                    pixels[offset + x] = result.get(x, y) ? bottomLeftColor: bgColor;
                }else{
                    pixels[offset  + x] = result.get(x, y) ? QRColor :bgColor;
                }
            }
        }

        Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,width,0,0,width,height);
        return bitmap;
    }
}
