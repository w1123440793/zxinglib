package com.wang.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.view.ScanQRcodeView;

/**
 * Author  wangchenchen
 * CreateDate 2017-6-7.
 * Email wcc@jusfoun.com
 * Description
 */
public class ScanQrCodeActivity extends AppCompatActivity {

    protected ScanQRcodeView scanQrCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_scan);
        initView();
    }

    private void initView() {
        scanQrCode = (ScanQRcodeView) findViewById(R.id.scan_qr_code);
        scanQrCode.setOnScanListener(new ScanQRcodeView.OnScanListener() {
            @Override
            public void onScanSuc(String result) {
                setResult(RESULT_OK,getIntent().putExtra("qr_code_result",result));
                onBackPressed();
            }

            @Override
            public void onScanFail(String fail) {

            }
        });
    }
}
