package com.feiqiqi.likeweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feiqiqi.likeweather.gson.Forecast;
import com.feiqiqi.likeweather.gson.Weather;
import com.feiqiqi.likeweather.util.HttpUtil;
import com.feiqiqi.likeweather.util.Utility;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherScrollView;

    private TextView cityTitle;

    private TextView titleUpdateTime;

    private TextView degreeTxt;

    private TextView weatherInfoTxt;

    private LinearLayout forecastLayout;

    private TextView aqiTxt;
    private TextView pm25Txt;
    private TextView comfortTxt;
    private TextView carWashTxt;
    private TextView sportTxt;

    private ImageView bing_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 背景填充状态栏*/
        //版本号>21即5.0及以上系统
        /*if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //活动的布局会显示在状态栏
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        setContentView(R.layout.activity_weather);

        //沉浸式状态栏
        ImmersionBar.with(this).init();

        //获得控件实例
        weatherScrollView = (ScrollView) findViewById(R.id.weather_scrollView);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        cityTitle = (TextView) findViewById(R.id.city_title);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeTxt = (TextView) findViewById(R.id.degree_txt);
        weatherInfoTxt = (TextView) findViewById(R.id.weather_info_txt);
        aqiTxt = (TextView) findViewById(R.id.aqi_txt);
        pm25Txt = (TextView) findViewById(R.id.pm25_txt);
        comfortTxt = (TextView) findViewById(R.id.comfort_txt);
        carWashTxt = (TextView) findViewById(R.id.car_wash_txt);
        sportTxt = (TextView) findViewById(R.id.sport_txt);

        bing_img = (ImageView) findViewById(R.id.bing_img);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = preferences.getString("weather", null);
        if (weatherStr != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatheResponse(weatherStr);
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询数据
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherScrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        /**
         * 背景更换*/
        /*String bingImg = preferences.getString("bing_pic", null);
        if (bingImg != null) {

            Glide.with(this).load(bingImg).into(bing_img);
        } else {
            loadBingImg();
        }*/


    }

    /**
     * 加载必应每日一图
     */
    private void loadBingImg() {

        String requestBingImg = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingImg, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingImg = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this
                ).edit();
                editor.putString("bing_pic", bingImg);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingImg).into(bing_img);
                    }
                });
            }
        });

    }

    /**
     * 根据天气id请求天气数据
     */
    private void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=bc0418b57b2d4918819d3974ac1285d9";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseTxt = response.body().string();
                final Weather weather = Utility.handleWeatheResponse(responseTxt);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    WeatherActivity.this).edit();
                            editor.putString("weather", responseTxt);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        cityTitle.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeTxt.setText(degree);
        weatherInfoTxt.setText(weatherInfo);

        forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {

            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateTxt = (TextView) view.findViewById(R.id.date_text);
            TextView infoTxt = (TextView) view.findViewById(R.id.info_text);
            TextView maxTxt = (TextView) view.findViewById(R.id.max_text);
            TextView minTxt = (TextView) view.findViewById(R.id.min_text);

            dateTxt.setText(forecast.date);
            infoTxt.setText(forecast.more.info);
            maxTxt.setText(forecast.temperature.max);
            minTxt.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiTxt.setText(weather.aqi.city.aqi);
            pm25Txt.setText(weather.aqi.city.pm25);

        }

        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;

        comfortTxt.setText(comfort);
        carWashTxt.setText(carWash);
        sportTxt.setText(sport);
        weatherScrollView.setVisibility(View.VISIBLE);
    }
}
