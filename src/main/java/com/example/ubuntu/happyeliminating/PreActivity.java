package com.example.ubuntu.happyeliminating;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;

public class PreActivity extends Activity {
    //获取读取权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 3) {
                    Intent intent = new Intent(PreActivity.this, MainActivity.class);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //销毁当前活动
                    finish();
                }
            }
        };
    @Override
    public void onCreate(Bundle savedInstanceState) {

        System.out.println("pppppp");
        super.onCreate(savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(PreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int Wpre = ActivityCompat.checkSelfPermission(PreActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED||Wpre != PackageManager.PERMISSION_GRANTED) {
            System.out.println("GET PREMISSSION");
            ActivityCompat.requestPermissions(PreActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(PreActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        System.out.println("111111");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        System.out.println("00000");
        setContentView(R.layout.activity_pre);
        System.out.println("88888");
        WaitThread thread;
        thread = new WaitThread();
        thread.start();
        System.out.println("99999");
    }
    public class WaitThread extends Thread {
        @Override
        public void run() {
            System.out.println("before sleep");
            try {
                this.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("6666");
            Message message;
            message= new Message();
            message.what = 3;
            message.arg1 = 1;
            System.out.println("3333");
            handler.sendMessage(message);
        }
    }
}
