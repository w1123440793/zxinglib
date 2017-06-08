package com.wang.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int SCAN_QR_CODE = 10001;
    protected Button scanQrCode;
    protected Button makeQrCode;
    protected TextView resultTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SCAN_QR_CODE:
                String result = data.getStringExtra("qr_code_result");
                resultTxt.setText(result);
        }
    }

    private void initView() {
        scanQrCode = (Button) findViewById(R.id.scan_qr_code);
        makeQrCode = (Button) findViewById(R.id.make_qr_code);
        resultTxt = (TextView) findViewById(R.id.result);

        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ScanQrCodeActivity.class);
                startActivityForResult(intent,SCAN_QR_CODE);
            }
        });

        makeQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MakeQrActivity.class);
                startActivity(intent);
            }
        });
    }
}
