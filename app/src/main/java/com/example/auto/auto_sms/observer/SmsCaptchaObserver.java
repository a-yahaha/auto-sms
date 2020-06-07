package com.example.auto.auto_sms.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 *  短信验证码监听器
 */

public class SmsCaptchaObserver extends ContentObserver {

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
}
