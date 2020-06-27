package com.example.auto.auto_sms.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  短信验证码监听器
 *  {@link <a href="https://juejin.im/post/5b207bc05188257d367e6c63">自动接收短信验证码</a>}
 */

public class SmsCaptchaObserver extends ContentObserver {

    private static final Uri SMS_INBOX = Uri.parse("content://sms/");

    /**
     * 上下文
     */
    private Context context;

    /**
     * 短信结果处理器
     */
    private SmsListener smsListener;

    private Cursor mCursor;

    public SmsCaptchaObserver(Context context, Handler handler, SmsListener smsListener) {
        super(handler);
        this.context = context;
        this.smsListener = smsListener;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        //
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        readLatestSms();

        readAllSms();



    }

    /**
     * 阅读最新的sms
     */
    private void readLatestSms() {
        // 第二次回调 查询收件箱里的内容
        Uri inboxUri = Uri.parse("content://sms/inbox");
        // 按时间顺序排序短信数据库
        mCursor = context.getContentResolver().query(inboxUri, null, null,
                null, "date desc");

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                // 获取手机号
                String address = mCursor.getString(mCursor.getColumnIndex("address"));
                // 获取短信内容
                String body = mCursor.getString(mCursor.getColumnIndex("body"));
                Log.e("SmsCaptchaObserver", "address: " + address + " ; "
                        + " body: " + body);
                if (smsListener != null) {
                    smsListener.onResult(body);
                }
            }
        }
        mCursor.close();
    }

    /**
     * 读取所有的数据库数据
     */
    private void readAllSms() {

        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = context.getContentResolver().query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("readAllSms", "cur == null");
            return;
        }
        List<Map<String, Object>> smsList = new ArrayList<>();
        while (cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex("_id"));//手机号
            String address = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            Log.e("SmsCaptchaObserver", " id: " + id + "  ; "
                    + " address: " + address + " ; "
                    + " name: " + name + " ; "
                    + " body: " + body);


            Map<String, Object> sms = new HashMap<>();
            sms.put("id", id);
            sms.put("address", address);
            sms.put("name", name);
            sms.put("body", body);

            smsList.add(sms);
        }
        cur.close();

        smsListener.onResult(JSON.toJSONString(smsList));
    }
}
