package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 引用天气信息对应的5个实体类
 */
public class Weather {


    /**
     * 成功返回ok,失败返回具体原因
     */
    public String status;

    public AQi aqi;

    public Basic basic;

    public Now now;

    public Suggestion suggestion;


    /**
     * daily_forecast中包含的是一个数组
     */
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
