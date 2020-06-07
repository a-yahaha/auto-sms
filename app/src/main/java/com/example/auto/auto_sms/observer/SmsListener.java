package com.example.auto.auto_sms.observer;


/**
 * 短信处理器
 */
public interface SmsListener {

    /**
     * 处理返回结果
     * @param result
     */
    void onResult(String result);
}
