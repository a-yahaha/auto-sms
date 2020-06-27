package com.example.auto.auto_sms;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.auto.auto_sms.observer.SmsCaptchaObserver;
import com.example.auto.auto_sms.observer.SmsListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 注册短信变化监听
        this.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"),
                true,
                new SmsCaptchaObserver(this, new Handler(), new SmsListener() {
                    @Override
                    public void onResult(String result) {
                        // TODO 这里做短信的上报
                        Log.i("SmsListener onResult", result);
                    }
                }));
    }

}
