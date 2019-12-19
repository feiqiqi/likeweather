package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    /**
     * 城市名称
     */
    @SerializedName("city")
    public String cityName;

    /**
     * 城市id
     */
    @SerializedName("id")
    public String weatherId;


    public Update update;

    public class Update {

        /**
         * 天气更新时间
         */
        @SerializedName("loc")
        public String updateTime;
    }
}
