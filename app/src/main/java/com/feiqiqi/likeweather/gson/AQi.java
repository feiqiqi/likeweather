package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 空气质量指数
 */

public class AQi {


    public AQICity city;

    public class AQICity {
        /**
         * 城市空气质量指数
         */
        @SerializedName("aqi")
        public String aqi;

        /**
         * pm2.5
         */

        @SerializedName("pm25")
        public String pm25;
    }
}
