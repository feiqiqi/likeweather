package com.feiqiqi.likeweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * 与服务器交互
 */
public class HttpUtil {

    /*由于OkHttp出色的封装，我们发出一个请求秩序调用sendOkHttpRequest()方法，
    传入请求地址，并注册一个回调来处理服务器响应即可 */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
