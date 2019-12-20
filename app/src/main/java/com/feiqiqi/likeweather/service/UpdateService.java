package com.feiqiqi.likeweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.feiqiqi.likeweather.WeatherActivity;
import com.feiqiqi.likeweather.gson.Weather;
import com.feiqiqi.likeweather.util.HttpUtil;
import com.feiqiqi.likeweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updateBingPic();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 5 * 60 * 60 * 1000;  //5小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent(this, UpdateService.class);

        PendingIntent pi = PendingIntent.getService(this, 0, intent1, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 更新每日一图
     */
    private void updateBingPic() {
        String requestBingImg = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingImg, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingImg = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this
                ).edit();
                editor.putString("bing_pic", bingImg);
                editor.apply();

            }
        });

    }

    /**
     * 更新天气数据
     */
    private void updateWeather() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sharedPreferences.getString("weather", null);
        if (weatherStr != null) {
            //有缓存时直接解析数据
            Weather weather = Utility.handleWeatheResponse(weatherStr);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/weather?cityid=" +
                    weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseTxt = response.body().string();
                    final Weather weather = Utility.handleWeatheResponse(responseTxt);

                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                UpdateService.this).edit();
                        editor.putString("weather", responseTxt);
                        editor.apply();
                    }
                }
            });
        }
    }
}
