package com.wang.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.utils.WiterQRUtil;

/**
 * Author  wangchenchen
 * CreateDate 2017-6-8.
 * Email wcc@jusfoun.com
 * Description
 */
public class MakeQrActivity extends AppCompatActivity {

    protected EditText edit;
    protected ImageView qrImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.layout_make_qr);
        initView();
    }

    private void initView() {
        edit = (EditText) findViewById(R.id.edit);
        qrImage = (ImageView) findViewById(R.id.qr_image);

        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (!TextUtils.isEmpty(edit.getText())){
                        Bitmap bitmap=WiterQRUtil.witerQRCenterLogo(edit.getText().toString(),
                                BitmapFactory.decodeResource(getResources(),R.mipmap.logo)
                                ,500, Color.parseColor("#ff000000"),Color.parseColor("#ffffffff"));
                        qrImage.setImageBitmap(bitmap);
                        return true;
                    }
                }

                return false;
            }
        });
    }
}
