package com.feiqiqi.likeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实时天气
 */
public class Now {

    /**
     * 温度
     */
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        /**
         * 状态：天晴，下雨，阵雨...
         */
        @SerializedName("txt")
        public String info;
    }
}
